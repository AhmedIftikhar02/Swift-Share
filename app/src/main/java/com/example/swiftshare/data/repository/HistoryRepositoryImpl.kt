package com.example.swiftshare.data.repository

import com.example.swiftshare.common.providers.DispatcherProvider
import com.example.swiftshare.core.result.AppException
import com.example.swiftshare.core.result.Result
import com.example.swiftshare.database.dao.TransferSessionDao
import com.example.swiftshare.database.mapper.toDomain
import com.example.swiftshare.database.mapper.toEntity
import com.example.swiftshare.domain.model.SessionStatus
import com.example.swiftshare.domain.model.TransferDirection
import com.example.swiftshare.domain.model.TransferSessionModel
import com.example.swiftshare.domain.repository.HistoryFilter
import com.example.swiftshare.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryRepositoryImpl @Inject constructor(
    private val dao: TransferSessionDao,
    private val dispatcherProvider: DispatcherProvider
) : HistoryRepository {

    override fun observeHistory(filter: HistoryFilter): Flow<List<TransferSessionModel>> =
        dao.observeAllSessionsWithFiles()
            .map { rows ->

                rows.mapNotNull { row -> runCatching { row.toDomain() }.onFailure {
                    Timber.tag("HistoryRepositoryImpl").e(it, "Skipping malformed history row")
                }.getOrNull() }
            }
            .map { sessions -> sessions.applyFilter(filter) }
            .flowOn(dispatcherProvider.io)

    override suspend fun getSessionDetail(sessionId: String): Result<TransferSessionModel> =
        withContext(dispatcherProvider.io) {
            val row = runCatching { dao.getSessionWithFiles(sessionId) }.getOrNull()
            val domain = row?.let { runCatching { it.toDomain() }.getOrNull() }
            if (domain != null) Result.Success(domain)
            else Result.Error(AppException.UnknownError("Transfer record not found."))
        }

    override suspend fun deleteSession(sessionId: String): Result<Unit> =
        withContext(dispatcherProvider.io) {
            runCatching { dao.deleteSession(sessionId) }
                .fold(
                    onSuccess = { Result.Success(Unit) },
                    onFailure = { Result.Error(AppException.UnknownError("Could not delete this record.")) }
                )
        }

    override suspend fun saveSession(session: TransferSessionModel): Result<Unit> =
        withContext(dispatcherProvider.io) {
            runCatching {
                dao.insertSessionWithFiles(session.toEntity(), session.files.map { it.toEntity(session.sessionId) })
            }.fold(
                onSuccess = { Result.Success(Unit) },
                onFailure = { Result.Error(AppException.UnknownError("Could not save this transfer to history.")) }
            )
        }

    private fun List<TransferSessionModel>.applyFilter(filter: HistoryFilter): List<TransferSessionModel> =
        when (filter) {
            HistoryFilter.ALL -> this
            HistoryFilter.SENT -> filter { it.direction == TransferDirection.SENT }
            HistoryFilter.RECEIVED -> filter { it.direction == TransferDirection.RECEIVED }
            HistoryFilter.FAILED -> filter { it.status == SessionStatus.FAILED || it.status == SessionStatus.PARTIAL }
        }
}