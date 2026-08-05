package com.example.swiftshare.domain.repository

import com.example.swiftshare.core.result.Result
import com.example.swiftshare.domain.model.FileTransferStatus
import com.example.swiftshare.domain.model.TransferSessionModel
import kotlinx.coroutines.flow.Flow

enum class HistoryFilter { ALL, SENT, RECEIVED, FAILED }

interface HistoryRepository {
    fun observeHistory(filter: HistoryFilter = HistoryFilter.ALL): Flow<List<TransferSessionModel>>
    suspend fun getSessionDetail(sessionId: String): Result<TransferSessionModel>
    suspend fun deleteSession(sessionId: String): Result<Unit>

    suspend fun saveSession(session: TransferSessionModel): Result<Unit>

    /**
     * Phase 8: looks up which session a given file belongs to. Used by
     * [TransferRepository.retryFile] when the file is no longer part of the in-memory
     * active session (e.g. retried later from History once Phase 10 wires that screen up).
     */
    suspend fun findSessionIdForFile(fileTransferId: String): String?

    /**
     * Phase 8: lightweight single-file status update, avoiding a full session re-save when
     * only one file's outcome changed (e.g. a retry that failed re-validation).
     */
    suspend fun updateFileStatus(fileTransferId: String, status: FileTransferStatus, errorCode: String?): Result<Unit>
}