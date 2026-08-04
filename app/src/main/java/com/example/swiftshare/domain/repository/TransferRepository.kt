package com.example.swiftshare.domain.repository

import com.example.swiftshare.core.result.Result
import com.example.swiftshare.domain.model.QueuedFileModel
import com.example.swiftshare.domain.model.TransferSessionModel
import kotlinx.coroutines.flow.Flow

interface TransferRepository {

    fun observeActiveSession(): Flow<TransferSessionModel?>

    fun observeQueue(): Flow<List<QueuedFileModel>>
    suspend fun queueFiles(endpointId: String, fileUris: List<String>): Result<Unit>
    suspend fun removeFromQueue(uri: String)
    suspend fun clearQueue()


    suspend fun startTransfer(sessionId: String): Result<Unit>

    suspend fun pauseTransfer(sessionId: String): Result<Unit>
    suspend fun resumeTransfer(sessionId: String): Result<Unit>

    suspend fun cancelTransfer(sessionId: String): Result<Unit>

    suspend fun retryFile(fileTransferId: String): Result<Unit>
}