package com.example.swiftshare.domain.repository

import com.example.swiftshare.core.result.Result
import com.example.swiftshare.domain.model.QueuedFileModel
import com.example.swiftshare.domain.model.TransferSessionModel
import kotlinx.coroutines.flow.Flow

/**
 * Orchestrates an in-progress transfer session: queueing, streaming, and control actions.
 * Queueing is implemented in Phase 6; streaming/control land in Phase 7/8.
 */
interface TransferRepository {
    fun observeActiveSession(): Flow<TransferSessionModel?>

    /** Current queue awaiting confirmation, updated as files resolve/are removed (PRD 2.7). */
    fun observeQueue(): Flow<List<QueuedFileModel>>

    /** Adds the given URIs to the queue, resolving metadata asynchronously (PRD 2.6). Returns
     *  the count of URIs that were immediately excluded as inaccessible. */
    suspend fun queueFiles(endpointId: String, fileUris: List<String>): Result<Unit>

    suspend fun removeFromQueue(uri: String)
    suspend fun clearQueue()

    suspend fun startTransfer(sessionId: String): Result<Unit>
    suspend fun pauseTransfer(sessionId: String): Result<Unit>
    suspend fun resumeTransfer(sessionId: String): Result<Unit>
    suspend fun cancelTransfer(sessionId: String): Result<Unit>
    suspend fun retryFile(fileTransferId: String): Result<Unit>
}