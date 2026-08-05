package com.example.swiftshare.data.transfer

import android.content.ContentResolver
import android.net.Uri
import com.example.swiftshare.common.providers.DispatcherProvider
import com.example.swiftshare.core.nearby.FileMetadataCodec
import com.example.swiftshare.core.nearby.TransferFileMetadata
import com.example.swiftshare.data.nearby.NearbyConnectionsDataSource
import com.example.swiftshare.domain.model.PayloadEvent
import com.example.swiftshare.domain.model.PayloadStatus
import com.example.swiftshare.domain.model.QueuedFileModel
import com.example.swiftshare.domain.model.TransferProgress
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.ArrayDeque
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileTransferDataSource @Inject constructor(
    private val nearbyDataSource: NearbyConnectionsDataSource,
    private val contentResolver: ContentResolver,
    private val receivedFileSaver: ReceivedFileSaver,
    private val dispatcherProvider: DispatcherProvider
) {
    private val activeSendPayloads = mutableMapOf<String, Long>()

    fun sendFiles(
        endpointId: String,
        sessionId: String,
        files: List<Pair<String, QueuedFileModel>>
    ): Flow<TransferProgress> = flow {
        for ((fileTransferId, file) in files) {
            val payloadId = try {
                val metadata = TransferFileMetadata(
                    sessionId = sessionId,
                    fileTransferId = fileTransferId,
                    fileName = file.fileName,
                    mimeType = file.mimeType,
                    sizeBytes = file.sizeBytes
                )
                nearbyDataSource.sendBytesPayload(endpointId, FileMetadataCodec.encode(metadata))

                val pfd = contentResolver.openFileDescriptor(Uri.parse(file.uri), "r")
                    ?: throw IllegalStateException("\"${file.fileName}\" is no longer accessible.")

                nearbyDataSource.sendFilePayload(endpointId, pfd).also {
                    activeSendPayloads[fileTransferId] = it
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Timber.tag("FileTransferDataSource").w(e, "Failed to start sending %s", file.fileName)
                emit(TransferProgress(fileTransferId, 0L, file.sizeBytes, PayloadStatus.FAILURE))
                null
            } ?: continue

            emit(TransferProgress(fileTransferId, 0L, file.sizeBytes, PayloadStatus.IN_PROGRESS))

            var terminalReached = false
            nearbyDataSource.payloadEvents
                .filterIsInstance<PayloadEvent.TransferUpdate>()
                .filter { it.payloadId == payloadId }
                .collect { update ->
                    if (terminalReached) return@collect
                    emit(TransferProgress(fileTransferId, update.bytesTransferred, update.totalBytes, update.status))
                    if (update.status != PayloadStatus.IN_PROGRESS) terminalReached = true
                }

            activeSendPayloads.remove(fileTransferId)
        }
    }.flowOn(dispatcherProvider.io)

    fun receiveFiles(endpointId: String): Flow<TransferProgress> = callbackFlow {
        val pendingMetadata = ArrayDeque<TransferFileMetadata>()
        val activeReceives = mutableMapOf<Long, TransferFileMetadata>()

        val job = kotlinx.coroutines.CoroutineScope(dispatcherProvider.io).launch {
            nearbyDataSource.payloadEvents.collect { event ->
                try {
                    when (event) {
                        is PayloadEvent.BytesReceived -> {
                            if (event.endpointId != endpointId) return@collect
                            FileMetadataCodec.decode(event.bytes)?.let { pendingMetadata.addLast(it) }
                        }

                        is PayloadEvent.FileIncomingStarted -> {
                            if (event.endpointId != endpointId) return@collect
                            val metadata = pendingMetadata.pollFirst() ?: return@collect
                            activeReceives[event.payloadId] = metadata
                            trySend(
                                TransferProgress(
                                    fileTransferId = metadata.fileTransferId,
                                    bytesTransferred = 0L,
                                    totalBytes = metadata.sizeBytes,
                                    status = PayloadStatus.IN_PROGRESS,
                                    fileName = metadata.fileName,
                                    mimeType = metadata.mimeType
                                )
                            )
                        }

                        is PayloadEvent.TransferUpdate -> {
                            val metadata = activeReceives[event.payloadId] ?: return@collect

                            when (event.status) {
                                PayloadStatus.SUCCESS -> {
                                    val rawUri = nearbyDataSource.takeReceivedFileUri(event.payloadId)
                                    val savedUri = rawUri?.let {
                                        receivedFileSaver.saveToDownloads(it, metadata.fileName, metadata.mimeType)
                                    }
                                    activeReceives.remove(event.payloadId)
                                    trySend(
                                        TransferProgress(
                                            fileTransferId = metadata.fileTransferId,
                                            bytesTransferred = event.totalBytes,
                                            totalBytes = event.totalBytes,
                                            // A save failure (e.g. storage full) still shows as
                                            // FAILURE to the user even though the radio transfer
                                            // itself succeeded.
                                            status = if (savedUri != null) PayloadStatus.SUCCESS else PayloadStatus.FAILURE,
                                            fileName = metadata.fileName,
                                            mimeType = metadata.mimeType,
                                            savedUri = savedUri
                                        )
                                    )
                                }
                                PayloadStatus.FAILURE, PayloadStatus.CANCELED -> {
                                    // Phase 8: explicitly delete whatever partial bytes the SDK
                                    // had already written for this payload — previously this
                                    // Uri was discarded without cleanup (PRD 2.12 / PRD 13).
                                    val leftoverUri = nearbyDataSource.takeReceivedFileUri(event.payloadId)
                                    if (leftoverUri != null) {
                                        receivedFileSaver.deleteIncompletePayload(leftoverUri)
                                    }
                                    activeReceives.remove(event.payloadId)
                                    trySend(
                                        TransferProgress(
                                            metadata.fileTransferId, event.bytesTransferred, event.totalBytes,
                                            event.status, metadata.fileName, metadata.mimeType
                                        )
                                    )
                                }
                                PayloadStatus.IN_PROGRESS -> trySend(
                                    TransferProgress(
                                        metadata.fileTransferId, event.bytesTransferred, event.totalBytes,
                                        PayloadStatus.IN_PROGRESS, metadata.fileName, metadata.mimeType
                                    )
                                )
                            }
                        }
                    }
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    Timber.tag("FileTransferDataSource").e(e, "Error handling incoming payload event")
                }
            }
        }

        awaitClose { job.cancel() }
    }

    fun cancelFile(fileTransferId: String) {
        activeSendPayloads[fileTransferId]?.let { nearbyDataSource.cancelPayload(it) }
        activeSendPayloads.remove(fileTransferId)
    }
}