package com.example.swiftshare.data.repository

import android.util.Log
import com.example.swiftshare.common.providers.DispatcherProvider
import com.example.swiftshare.core.result.AppException
import com.example.swiftshare.core.result.Result
import com.example.swiftshare.core.util.ChecksumUtil
import com.example.swiftshare.data.transfer.FileMetadataResolver
import com.example.swiftshare.data.transfer.FileTransferDataSource
import com.example.swiftshare.data.transfer.SourceFileValidator
import com.example.swiftshare.domain.model.ConnectionState
import com.example.swiftshare.domain.model.DeviceModel
import com.example.swiftshare.domain.model.FileTransferModel
import com.example.swiftshare.domain.model.FileTransferStatus
import com.example.swiftshare.domain.model.PayloadStatus
import com.example.swiftshare.domain.model.QueuedFileModel
import com.example.swiftshare.domain.model.SessionStatus
import com.example.swiftshare.domain.model.TransferDirection
import com.example.swiftshare.domain.model.TransferErrorCode
import com.example.swiftshare.domain.model.TransferProgress
import com.example.swiftshare.domain.model.TransferSessionModel
import com.example.swiftshare.domain.repository.HistoryRepository
import com.example.swiftshare.domain.repository.NearbyRepository
import com.example.swiftshare.domain.repository.SettingsRepository
import com.example.swiftshare.domain.repository.TransferRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import timber.log.Timber
import java.io.File
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * PHASE 6: queue management. PHASE 7: real transfer execution (send/receive, progress).
 * PHASE 8: pause / resume / retry, plus a bounded reconnection helper shared by both.
 */
@Singleton
class TransferRepositoryImpl @Inject constructor(
    private val fileMetadataResolver: FileMetadataResolver,
    private val fileTransferDataSource: FileTransferDataSource,
    private val sourceFileValidator: SourceFileValidator,
    private val nearbyRepository: NearbyRepository,
    private val historyRepository: HistoryRepository,
    private val settingsRepository: SettingsRepository,
    private val dispatcherProvider: DispatcherProvider
) : TransferRepository {

    private companion object {
        // PRD 10.6: "bounded retry window" for rediscovering a previously known device.
        const val RECONNECT_WINDOW_MS = 25_000L
    }

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
                status = FileTransferStatus.QUEUED,
                sourceLastModified = q.lastModified
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

        currentEndpointId = endpointId
        launchResend(sessionId, endpointId, initialFiles.map { it.fileTransferId to it })
        return Result.Success(Unit)
    }

    // ===================== TRANSFER CONTROL (Phase 8) =====================

    override suspend fun pauseTransfer(sessionId: String): Result<Unit> {
        val session = activeSession.value
        if (session == null || session.sessionId != sessionId) {
            return Result.Error(AppException.UnknownError("This transfer is no longer active."))
        }
        if (session.direction != TransferDirection.SENT) {
            return Result.Error(AppException.UnknownError("Only transfers you're sending can be paused."))
        }
        if (session.status != SessionStatus.IN_PROGRESS) {
            return Result.Error(AppException.UnknownError("This transfer isn't currently in progress."))
        }

        Log.d("TransferRepo", "pauseTransfer: sessionId=$sessionId")
        activeSendJob?.cancel()

        val inFlightIds = session.files
            .filter { it.status == FileTransferStatus.QUEUED || it.status == FileTransferStatus.TRANSFERRING }
            .map { it.fileTransferId }
        inFlightIds.forEach { fileTransferDataSource.cancelFile(it) }

        val pausedFiles = session.files.map { f ->
            if (f.fileTransferId in inFlightIds) f.copy(status = FileTransferStatus.PAUSED) else f
        }
        val pausedSession = session.copy(status = SessionStatus.PAUSED, files = pausedFiles)
        activeSession.value = pausedSession
        persistSessionSnapshot(pausedSession)
        Log.d("TransferRepo", "Transfer paused")
        return Result.Success(Unit)
    }

    override suspend fun resumeTransfer(sessionId: String): Result<Unit> {
        val session = activeSession.value
        if (session == null || session.sessionId != sessionId) {
            return Result.Error(AppException.UnknownError("This transfer is no longer active."))
        }
        if (session.status != SessionStatus.PAUSED && session.status != SessionStatus.RECONNECTING) {
            return Result.Error(AppException.UnknownError("This transfer isn't paused."))
        }

        Log.d("TransferRepo", "resumeTransfer: sessionId=$sessionId")

        val connectResult = ensureConnectedToDevice(session.device)
        if (connectResult is Result.Error) return connectResult
        val connectedDevice = nearbyRepository.connectedDevice.value
            ?: return Result.Error(AppException.UnknownError("Connection was lost again. Try resuming once more."))

        // Nearby Connections can't resume a cancelled Payload mid-stream — every file that was
        // still QUEUED/TRANSFERRING at pause-time restarts from byte 0 (PRD 2.11).
        val resumeCandidates = session.files.filter { it.status == FileTransferStatus.PAUSED }
        val (validFiles, invalidFiles) = validateFilesForResend(resumeCandidates)

        activeSession.update { current ->
            current?.copy(
                status = SessionStatus.IN_PROGRESS,
                device = connectedDevice,
                files = current.files.map { f ->
                    when {
                        validFiles.any { it.fileTransferId == f.fileTransferId } ->
                            f.copy(status = FileTransferStatus.QUEUED, transferredBytes = 0L)
                        invalidFiles.any { it.fileTransferId == f.fileTransferId } ->
                            invalidFiles.first { it.fileTransferId == f.fileTransferId }
                        else -> f
                    }
                }
            )
        }

        currentEndpointId = connectedDevice.endpointId

        if (validFiles.isEmpty()) {
            finalizeSessionIfDone(sessionId)
        } else {
            launchResend(sessionId, connectedDevice.endpointId, validFiles.map { it.fileTransferId to it })
        }
        return Result.Success(Unit)
    }

    override suspend fun retryFile(fileTransferId: String): Result<Unit> {
        Log.d("TransferRepo", "retryFile: fileTransferId=$fileTransferId")

        val (session, file) = findFileAndSession(fileTransferId)
            ?: return Result.Error(AppException.UnknownError("This file record could no longer be found."))

        if (file.status != FileTransferStatus.FAILED && file.status != FileTransferStatus.CANCELLED) {
            return Result.Error(AppException.UnknownError("Only failed or cancelled files can be retried."))
        }
        if (session.direction != TransferDirection.SENT) {
            return Result.Error(AppException.UnknownError("Only files you sent can be retried from here."))
        }

        when (sourceFileValidator.validate(file)) {
            SourceFileValidator.ValidationResult.MissingOrInaccessible -> {
                updateFileTerminal(session.sessionId, fileTransferId, FileTransferStatus.FAILED, TransferErrorCode.SOURCE_FILE_MISSING)
                return Result.Error(AppException.UnknownError("\"${file.fileName}\" is no longer available on this device."))
            }
            SourceFileValidator.ValidationResult.Changed -> {
                updateFileTerminal(session.sessionId, fileTransferId, FileTransferStatus.FAILED, TransferErrorCode.SOURCE_FILE_CHANGED)
                return Result.Error(AppException.UnknownError("\"${file.fileName}\" has changed since it was queued. Re-select it to send the new version."))
            }
            SourceFileValidator.ValidationResult.Valid -> Unit
        }

        val connectResult = ensureConnectedToDevice(session.device)
        if (connectResult is Result.Error) {
            updateFileTerminal(session.sessionId, fileTransferId, FileTransferStatus.FAILED, TransferErrorCode.DEVICE_UNREACHABLE)
            return connectResult
        }
        val connectedDevice = nearbyRepository.connectedDevice.value
            ?: return Result.Error(AppException.UnknownError("Connection was lost again. Try retrying once more."))

        val retryFileModel = file.copy(status = FileTransferStatus.QUEUED, transferredBytes = 0L, errorCode = null)

        // Promote this back into the "active" session so ActiveTransferDetailViewModel picks
        // it up live, whether or not it was still the in-memory active session already.
        val baseSession = activeSession.value?.takeIf { it.sessionId == session.sessionId } ?: session
        activeSession.value = baseSession.copy(
            status = SessionStatus.IN_PROGRESS,
            endedAt = null,
            device = connectedDevice,
            files = if (baseSession.files.any { it.fileTransferId == fileTransferId }) {
                baseSession.files.map { if (it.fileTransferId == fileTransferId) retryFileModel else it }
            } else {
                baseSession.files + retryFileModel
            }
        )

        currentEndpointId = connectedDevice.endpointId
        launchResend(session.sessionId, connectedDevice.endpointId, listOf(retryFileModel.fileTransferId to retryFileModel))
        return Result.Success(Unit)
    }

    override suspend fun cancelTransfer(sessionId: String): Result<Unit> {
        Log.d("TransferRepo", "cancelTransfer: sessionId=$sessionId")

        val session = activeSession.value
        if (session == null || session.sessionId != sessionId) {
            return Result.Error(AppException.UnknownError("This transfer is no longer active."))
        }

        activeSendJob?.cancel()

        val inFlightIds = session.files
            .filter { it.status == FileTransferStatus.QUEUED || it.status == FileTransferStatus.TRANSFERRING || it.status == FileTransferStatus.PAUSED }
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
        persistSessionSnapshot(cancelledSession)
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

    // ===================== SHARED SEND HELPER (Phase 7/8) =====================

    private fun launchResend(sessionId: String, endpointId: String, files: List<Pair<String, FileTransferModel>>) {
        if (files.isEmpty()) return
        val idsToFiles = files.map { (id, f) ->
            id to QueuedFileModel(
                uri = f.uri,
                fileName = f.fileName,
                mimeType = f.mimeType,
                sizeBytes = f.totalBytes,
                lastModified = f.sourceLastModified,
                isMetadataResolved = true
            )
        }
        activeSendJob?.cancel()
        activeSendJob = repositoryScope.launch {
            Log.d("TransferRepo", "Sending ${idsToFiles.size} file(s) to endpoint: $endpointId")
            fileTransferDataSource.sendFiles(endpointId, sessionId, idsToFiles).collect { progress ->
                applyProgress(sessionId, progress, computeChecksumOnComplete = true)
            }
            finalizeSessionIfDone(sessionId)
        }
    }

    // ===================== PAUSE/RESUME/RETRY HELPERS (Phase 8) =====================

    private suspend fun validateFilesForResend(
        candidates: List<FileTransferModel>
    ): Pair<List<FileTransferModel>, List<FileTransferModel>> {
        val valid = mutableListOf<FileTransferModel>()
        val invalid = mutableListOf<FileTransferModel>()
        candidates.forEach { file ->
            when (sourceFileValidator.validate(file)) {
                SourceFileValidator.ValidationResult.Valid -> valid += file
                SourceFileValidator.ValidationResult.MissingOrInaccessible ->
                    invalid += file.copy(status = FileTransferStatus.FAILED, errorCode = TransferErrorCode.SOURCE_FILE_MISSING)
                SourceFileValidator.ValidationResult.Changed ->
                    invalid += file.copy(status = FileTransferStatus.FAILED, errorCode = TransferErrorCode.SOURCE_FILE_CHANGED)
            }
        }
        return valid to invalid
    }

    /**
     * Ensures [device] is the currently connected endpoint before resuming/retrying. If the
     * connection was lost, attempts a single bounded rediscovery + reconnection-request
     * window (PRD 10.6). This still surfaces the standard mutual Connection Confirmation
     * dialog on both ends (PRD 14 Security) — it is not a silent background reconnect, and
     * a fresh discovery session may assign the device a new endpoint id, so matching is done
     * by display name rather than the old endpoint id.
     */
    private suspend fun ensureConnectedToDevice(device: DeviceModel): Result<Unit> {
        val connected = nearbyRepository.connectedDevice.value
        if (connected?.endpointId == device.endpointId) return Result.Success(Unit)

        Log.d("TransferRepo", "Device not connected, starting bounded reconnection window for ${device.displayName}")
        activeSession.update { it?.copy(status = SessionStatus.RECONNECTING) }

        val localName = settingsRepository.observeDeviceDisplayName().first()
        val discoverResult = nearbyRepository.startDiscovery(localName)
        if (discoverResult is Result.Error) {
            activeSession.update { it?.copy(status = SessionStatus.PAUSED) }
            return discoverResult
        }

        val found = withTimeoutOrNull(RECONNECT_WINDOW_MS) {
            nearbyRepository.observeNearbyDevices()
                .map { devices -> devices.firstOrNull { it.displayName == device.displayName } }
                .filterNotNull()
                .first()
        }

        if (found == null) {
            activeSession.update { it?.copy(status = SessionStatus.PAUSED) }
            return Result.Error(AppException.UnknownError(
                "\"${device.displayName}\" wasn't found nearby. Move closer and try again."
            ))
        }

        val requestResult = nearbyRepository.requestConnection(found.endpointId)
        if (requestResult is Result.Error) {
            activeSession.update { it?.copy(status = SessionStatus.PAUSED) }
            return requestResult
        }

        val connectedInTime = withTimeoutOrNull(RECONNECT_WINDOW_MS) {
            nearbyRepository.connectionState.filter { it == ConnectionState.CONNECTED }.first()
        }

        return if (connectedInTime != null) {
            Result.Success(Unit)
        } else {
            activeSession.update { it?.copy(status = SessionStatus.PAUSED) }
            Result.Error(AppException.UnknownError(
                "Waiting for \"${device.displayName}\" to accept the reconnection request timed out."
            ))
        }
    }

    private suspend fun findFileAndSession(fileTransferId: String): Pair<TransferSessionModel, FileTransferModel>? {
        activeSession.value?.files?.firstOrNull { it.fileTransferId == fileTransferId }?.let { file ->
            return activeSession.value!! to file
        }
        val sessionId = historyRepository.findSessionIdForFile(fileTransferId) ?: return null
        val result = historyRepository.getSessionDetail(sessionId)
        if (result !is Result.Success) return null
        val file = result.data.files.firstOrNull { it.fileTransferId == fileTransferId } ?: return null
        return result.data to file
    }

    private suspend fun updateFileTerminal(
        sessionId: String,
        fileTransferId: String,
        status: FileTransferStatus,
        errorCode: String
    ) {
        activeSession.update { current ->
            if (current?.sessionId != sessionId) current
            else current.copy(files = current.files.map { f ->
                if (f.fileTransferId == fileTransferId) f.copy(status = status, errorCode = errorCode) else f
            })
        }
        historyRepository.updateFileStatus(fileTransferId, status, errorCode)
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
        persistSessionSnapshot(finishedSession)
    }

    private suspend fun persistSessionSnapshot(session: TransferSessionModel) {
        Log.d("TransferRepo", "Persisting session: ${session.sessionId}")
        when (val result = historyRepository.saveSession(session)) {
            is Result.Error -> Timber.tag("TransferRepositoryImpl")
                .w("Failed to persist session %s: %s", session.sessionId, result.exception.message)
            is Result.Success -> Log.d("TransferRepo", "Session snapshot persisted successfully")
        }
    }
}