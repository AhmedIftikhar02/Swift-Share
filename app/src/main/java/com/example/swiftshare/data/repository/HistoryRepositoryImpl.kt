package com.example.swiftshare.data.repository

import com.example.swiftshare.core.result.AppException
import com.example.swiftshare.core.result.Result
import com.example.swiftshare.domain.model.TransferSessionModel
import com.example.swiftshare.domain.repository.HistoryFilter
import com.example.swiftshare.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryRepositoryImpl @Inject constructor() : HistoryRepository {

    private val history = MutableStateFlow<List<TransferSessionModel>>(emptyList())

    override fun observeHistory(filter: HistoryFilter): Flow<List<TransferSessionModel>> = history

    override suspend fun getSessionDetail(sessionId: String): Result<TransferSessionModel> =
        Result.Error(AppException.UnknownError("History is not implemented until Phase 7/10."))

    override suspend fun deleteSession(sessionId: String): Result<Unit> = Result.Success(Unit)
}