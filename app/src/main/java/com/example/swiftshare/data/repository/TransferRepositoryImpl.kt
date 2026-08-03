package com.example.swiftshare.data.repository

import android.util.Log
import com.example.swiftshare.core.result.AppException
import com.example.swiftshare.core.result.Result
import com.example.swiftshare.data.transfer.FileMetadataResolver
import com.example.swiftshare.domain.model.QueuedFileModel
import com.example.swiftshare.domain.model.TransferSessionModel
import com.example.swiftshare.domain.repository.TransferRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

/** PHASE 6: real queueing on top of the Phase 1 placeholder. Transfer execution
 *  (startTransfer/pauseTransfer/etc.) remains a no-op stub, replaced for real in Phase 7/8. */
@Singleton
class TransferRepositoryImpl @Inject constructor(
    private val fileMetadataResolver: FileMetadataResolver
) : TransferRepository {

    private val activeSession = MutableStateFlow<TransferSessionModel?>(null)
    private val queue = MutableStateFlow<List<QueuedFileModel>>(emptyList())

    override fun observeActiveSession(): Flow<TransferSessionModel?> = activeSession
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
        queue.update { current -> current.filterNot { it.uri == uri } }
    }

    override suspend fun clearQueue() {
        queue.value = emptyList()
    }

    override suspend fun startTransfer(sessionId: String): Result<Unit> = Result.Success(Unit)
    override suspend fun pauseTransfer(sessionId: String): Result<Unit> = Result.Success(Unit)
    override suspend fun resumeTransfer(sessionId: String): Result<Unit> = Result.Success(Unit)
    override suspend fun cancelTransfer(sessionId: String): Result<Unit> = Result.Success(Unit)
    override suspend fun retryFile(fileTransferId: String): Result<Unit> = Result.Success(Unit)
}