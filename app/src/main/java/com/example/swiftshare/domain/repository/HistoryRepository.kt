package com.example.swiftshare.domain.repository

import com.example.swiftshare.core.result.Result
import com.example.swiftshare.domain.model.TransferSessionModel
import kotlinx.coroutines.flow.Flow

enum class HistoryFilter { ALL, SENT, RECEIVED, FAILED }

interface HistoryRepository {
    fun observeHistory(filter: HistoryFilter = HistoryFilter.ALL): Flow<List<TransferSessionModel>>
    suspend fun getSessionDetail(sessionId: String): Result<TransferSessionModel>
    suspend fun deleteSession(sessionId: String): Result<Unit>


    suspend fun saveSession(session: TransferSessionModel): Result<Unit>
}