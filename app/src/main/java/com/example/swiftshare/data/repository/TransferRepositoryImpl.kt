package com.example.swiftshare.data.repository

import android.util.Log
import com.example.swiftshare.common.providers.DispatcherProvider
import com.example.swiftshare.core.result.AppException
import com.example.swiftshare.core.result.Result
import com.example.swiftshare.core.util.ChecksumUtil
import com.example.swiftshare.data.transfer.FileMetadataResolver
import com.example.swiftshare.data.transfer.FileTransferDataSource
import com.example.swiftshare.domain.model.FileTransferModel
import com.example.swiftshare.domain.model.FileTransferStatus
import com.example.swiftshare.domain.model.PayloadStatus
import com.example.swiftshare.domain.model.QueuedFileModel
import com.example.swiftshare.domain.model.SessionStatus
import com.example.swiftshare.domain.model.TransferDirection
import com.example.swiftshare.domain.model.TransferProgress
import com.example.swiftshare.domain.model.TransferSessionModel
import com.example.swiftshare.domain.repository.HistoryRepository
import com.example.swiftshare.domain.repository.NearbyRepository
import com.example.swiftshare.domain.repository.TransferRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * PHASE 6 + PHASE 7: Real queueing AND real transfer execution.
 *
 * Phase 6: queue management (observeQueue, queueFiles, removeFromQueue, clearQueue)
 * Phase 7: real transfer execution (startTransfer, cancelTransfer, receiving, progress tracking)
 * Phase 8: pause/resume/retry (stubs for now)
 */
@Singleton
class TransferRepositoryImpl @Inject constructor(
    private val fileMetadataResolver: FileMetadataResolver,
    private val fileTransferDataSource: FileTransferDataSource,
    private val nearbyRepository: NearbyRepository,
    private val historyRepository: HistoryRepository,
    private val dispatcherProvider: DispatcherProvider
) : TransferRepository {

    private val repositoryScope = CoroutineScope(SupervisorJob() + dispatcherProvider.default)

    private val activeSession = MutableStateFlow<TransferSessionModel?>(null)
    private val queue = MutableStateFlow<List<QueuedFileModel>>(emptyList())

    private var currentEndpointId: String? = null
    private var activeSendJob: Job? = null
    private var receiveJob: Job? = null

    init {
        repositoryScope.launch {
            nearbyRepository.connectedDevice.collect { device ->
                Log.d("TransferRepo", "Connected device changed: $device")
                if (device != null) {
                    currentEndpointId = device.endpointId
                    startReceivingIfNeeded(device.endpointId)
                } else {
                    currentEndpointId = null
                    receiveJob?.cancel()
                    receiveJob = null
                    Log.d("TransferRepo", "No connected device, stopping receive")
                }
            }
        }
    }

    // ===================== QUEUE MANAGEMENT (Phase 6) =====================

    override fun observeActiveSession(): Flow<TransferSessionModel?> = activeSession.asStateFlow()
    override fun observeQueue(): Flow<List<QueuedFileModel>> = queue.asStateFlow()

    override suspend fun queueFiles(endpointId: String, fileUris: List<String>): Result<Unit> {
        Log.d("TransferRepo", "Queueing ${fileUris.size} files for endpoint: $endpointId")

        // Skip URIs already present
        val existingUris = queue.value.map { it.uri }.toSet()
        val newUris = fileUris.filterNot { it in existingUris }

        if (newUris.isEmpty()) {
            Log.d("TransferRepo", "All files already in queue")
            return Result.Success(Unit)
        }

        Log.d("TransferRepo", "Resolving ${newUris.size} new files")
        val resolved = fileMetadataResolver.resolveMany(newUris)
        val resolvedCount = resolved.count { it != null }
        val excludedCount = newUris.size - resolvedCount

        Log.d("TransferRepo", "Resolved $resolvedCount files, $excludedCount excluded")

        queue.update { current -> current + resolved.filterNotNull() }

        return if (excludedCount > 0 && resolvedCount == 0) {
            Result.Error(AppException.UnknownError("None of the selected files could be accessed."))
        } else {
            Result.Success(Unit)
        }
    }

    override suspend fun removeFromQueue(uri: String) {
        Log.d("TransferRepo", "Removing from queue: $uri")
        queue.update { current -> current.filterNot { it.uri == uri } }
    }

    override suspend fun clearQueue() {
        Log.d("TransferRepo", "Clearing queue")
        queue.value = emptyList()
    }

    // ===================== TRANSFER EXECUTION (Phase 7) =====================

    override suspend fun startTransfer(sessionId: String): Result<Unit> {
        Log.d("TransferRepo", "startTransfer: sessionId=$sessionId")

        val endpointId = currentEndpointId
            ?: return Result.Error(AppException.UnknownError("No connected device to send to."))

        val queuedFiles = queue.value
        if (queuedFiles.isEmpty()) {
            return Result.Error(AppException.UnknownError("Add at least one file before sending."))
        }

        val device = nearbyRepository.connectedDevice.value
            ?: return Result.Error(AppException.UnknownError("Device is no longer connected. Reconnect and try again."))

        val idsToFiles = queuedFiles.map { UUID.randomUUID().toString() to it }
        val initialFiles = idsToFiles.map { (id, q) ->
            FileTransferModel(
                fileTransferId = id,
                sessionId = sessionId,
                fileName = q.fileName,
                mimeType = q.mimeType,
                totalBytes = q.sizeBytes,
                transferredBytes = 0L,
                uri = q.uri,
                status = FileTransferStatus.QUEUED
            )
        }

        Log.d("TransferRepo", "Creating session with ${initialFiles.size} files")
        activeSession.value = TransferSessionModel(
            sessionId = sessionId,
            device = device,
            direction = TransferDirection.SENT,
            startedAt = System.currentTimeMillis(),
            endedAt = null,
            status = SessionStatus.IN_PROGRESS,
            files = initialFiles
        )
        clearQueue()

        activeSendJob?.cancel()
        activeSendJob = repositoryScope.launch {
            Log.d("TransferRepo", "Starting send for endpoint: $endpointId")
            fileTransferDataSource.sendFiles(endpointId, sessionId, idsToFiles).collect { progress ->
                Log.d("TransferRepo", "Send progress: ${progress.fileTransferId} - ${progress.bytesTransferred}/${progress.totalBytes}")
                applyProgress(sessionId, progress, computeChecksumOnComplete = true)
            }
            finalizeSessionIfDone(sessionId)
        }
        return Result.Success(Unit)
    }

    override suspend fun pauseTransfer(sessionId: String): Result<Unit> {
        Log.d("TransferRepo", "pauseTransfer: sessionId=$sessionId (Phase 8 stub)")
        return Result.Success(Unit) // Phase 8
    }

    override suspend fun resumeTransfer(sessionId: String): Result<Unit> {
        Log.d("TransferRepo", "resumeTransfer: sessionId=$sessionId (Phase 8 stub)")
        return Result.Success(Unit) // Phase 8
    }

    override suspend fun retryFile(fileTransferId: String): Result<Unit> {
        Log.d("TransferRepo", "retryFile: fileTransferId=$fileTransferId (Phase 8 stub)")
        return Result.Success(Unit) // Phase 8
    }

    override suspend fun cancelTransfer(sessionId: String): Result<Unit> {
        Log.d("TransferRepo", "cancelTransfer: sessionId=$sessionId")

        val session = activeSession.value
        if (session == null || session.sessionId != sessionId) {
            return Result.Error(AppException.UnknownError("This transfer is no longer active."))
        }

        activeSendJob?.cancel()

        val inFlightIds = session.files
            .filter { it.status == FileTransferStatus.QUEUED || it.status == FileTransferStatus.TRANSFERRING }
            .map { it.fileTransferId }

        inFlightIds.forEach { fileTransferDataSource.cancelFile(it) }

        val cancelledFiles = session.files.map { f ->
            if (f.fileTransferId in inFlightIds) f.copy(status = FileTransferStatus.CANCELLED) else f
        }
        val cancelledSession = session.copy(
            status = SessionStatus.CANCELLED,
            endedAt = System.currentTimeMillis(),
            files = cancelledFiles
        )
        activeSession.value = cancelledSession
        persistFinishedSession(cancelledSession)
        Log.d("TransferRepo", "Transfer cancelled successfully")
        return Result.Success(Unit)
    }

    // ===================== RECEIVING (Phase 7) =====================

    private fun startReceivingIfNeeded(endpointId: String) {
        if (receiveJob?.isActive == true) {
            Log.d("TransferRepo", "Receive already active")
            return
        }
        Log.d("TransferRepo", "Starting receive for endpoint: $endpointId")
        receiveJob = repositoryScope.launch {
            fileTransferDataSource.receiveFiles(endpointId).collect { progress ->
                Log.d("TransferRepo", "Receive progress: ${progress.fileTransferId} - ${progress.bytesTransferred}/${progress.totalBytes}")
                onReceivedProgress(endpointId, progress)
            }
        }
    }

    private var receiveFinalizeJob: Job? = null

    private fun onReceivedProgress(endpointId: String, progress: TransferProgress) {
        val device = nearbyRepository.connectedDevice.value ?: return

        activeSession.update { current ->
            val base = if (current != null &&
                current.direction == TransferDirection.RECEIVED &&
                current.status == SessionStatus.IN_PROGRESS
            ) current else TransferSessionModel(
                sessionId = UUID.randomUUID().toString(),
                device = device,
                direction = TransferDirection.RECEIVED,
                startedAt = System.currentTimeMillis(),
                endedAt = null,
                status = SessionStatus.IN_PROGRESS,
                files = emptyList()
            )

            val fileExists = base.files.any { it.fileTransferId == progress.fileTransferId }
            val updatedFiles = if (fileExists) {
                base.files.map { f ->
                    if (f.fileTransferId != progress.fileTransferId) f
                    else f.copy(
                        transferredBytes = progress.bytesTransferred,
                        status = mapPayloadStatus(progress.status),
                        uri = progress.savedUri ?: f.uri
                    )
                }
            } else {
                base.files + FileTransferModel(
                    fileTransferId = progress.fileTransferId,
                    sessionId = base.sessionId,
                    fileName = progress.fileName,
                    mimeType = progress.mimeType,
                    totalBytes = progress.totalBytes,
                    transferredBytes = progress.bytesTransferred,
                    uri = progress.savedUri.orEmpty(),
                    status = mapPayloadStatus(progress.status)
                )
            }
            base.copy(files = updatedFiles)
        }

        // No explicit "batch complete" signal exists on the wire — a short inactivity window
        // after the most recent file's terminal status is used to decide the receiving session
        // is finished.
        if (progress.status != PayloadStatus.IN_PROGRESS) {
            receiveFinalizeJob?.cancel()
            receiveFinalizeJob = repositoryScope.launch {
                kotlinx.coroutines.delay(4_000L)
                activeSession.value?.let { session ->
                    if (session.direction == TransferDirection.RECEIVED && session.status == SessionStatus.IN_PROGRESS) {
                        Log.d("TransferRepo", "Receive session idle, finalizing")
                        finalizeSessionIfDone(session.sessionId)
                    }
                }
            }
        }
    }

    // ===================== PROGRESS APPLICATION (Shared by send + receive) =====================

    private fun applyProgress(sessionId: String, progress: TransferProgress, computeChecksumOnComplete: Boolean) {
        activeSession.update { session ->
            if (session == null || session.sessionId != sessionId) return@update session
            session.copy(files = session.files.map { f ->
                if (f.fileTransferId != progress.fileTransferId) f
                else {
                    val checksum = if (computeChecksumOnComplete && progress.status == PayloadStatus.SUCCESS) {
                        runCatching { ChecksumUtil.crc32Of(File(f.uri.removePrefix("file://"))) }.getOrNull()
                    } else f.checksum
                    f.copy(
                        transferredBytes = progress.bytesTransferred,
                        status = mapPayloadStatus(progress.status),
                        checksum = checksum ?: f.checksum
                    )
                }
            })
        }
    }

    private fun mapPayloadStatus(status: PayloadStatus): FileTransferStatus = when (status) {
        PayloadStatus.IN_PROGRESS -> FileTransferStatus.TRANSFERRING
        PayloadStatus.SUCCESS -> FileTransferStatus.COMPLETED
        PayloadStatus.FAILURE -> FileTransferStatus.FAILED
        PayloadStatus.CANCELED -> FileTransferStatus.CANCELLED
    }

    private suspend fun finalizeSessionIfDone(sessionId: String) {
        val session = activeSession.value ?: return
        if (session.sessionId != sessionId) return

        val allTerminal = session.files.isNotEmpty() && session.files.all {
            it.status == FileTransferStatus.COMPLETED ||
                    it.status == FileTransferStatus.FAILED ||
                    it.status == FileTransferStatus.CANCELLED
        }
        if (!allTerminal) return

        val finalStatus = when {
            session.files.all { it.status == FileTransferStatus.COMPLETED } -> SessionStatus.COMPLETED
            session.files.any { it.status == FileTransferStatus.COMPLETED } -> SessionStatus.PARTIAL
            else -> SessionStatus.FAILED
        }
        val finishedSession = session.copy(status = finalStatus, endedAt = System.currentTimeMillis())
        activeSession.value = finishedSession
        Log.d("TransferRepo", "Session finalized: ${finishedSession.sessionId} with status $finalStatus")
        persistFinishedSession(finishedSession)
    }

    private suspend fun persistFinishedSession(session: TransferSessionModel) {
        Log.d("TransferRepo", "Persisting session: ${session.sessionId}")
        when (val result = historyRepository.saveSession(session)) {
            is Result.Error -> Timber.tag("TransferRepositoryImpl")
                .w("Failed to persist session %s: %s", session.sessionId, result.exception.message)
            is Result.Success -> Log.d("TransferRepo", "Session persisted successfully")
        }
    }
}