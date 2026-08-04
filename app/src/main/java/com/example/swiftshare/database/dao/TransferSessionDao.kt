package com.example.swiftshare.database.dao

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import com.example.swiftshare.database.entity.FileTransferEntity
import com.example.swiftshare.database.entity.TransferSessionEntity
import kotlinx.coroutines.flow.Flow

data class SessionWithFiles(
    @Embedded val session: TransferSessionEntity,
    @Relation(parentColumn = "sessionId", entityColumn = "sessionId")
    val files: List<FileTransferEntity>
)

@Dao
interface TransferSessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: TransferSessionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFiles(files: List<FileTransferEntity>)
    @Transaction
    suspend fun insertSessionWithFiles(session: TransferSessionEntity, files: List<FileTransferEntity>) {
        insertSession(session)
        if (files.isNotEmpty()) insertFiles(files)
    }

    @Transaction
    @Query("SELECT * FROM transfer_sessions ORDER BY startedAt DESC")
    fun observeAllSessionsWithFiles(): Flow<List<SessionWithFiles>>

    @Transaction
    @Query("SELECT * FROM transfer_sessions WHERE sessionId = :sessionId LIMIT 1")
    suspend fun getSessionWithFiles(sessionId: String): SessionWithFiles?

    @Query("DELETE FROM transfer_sessions WHERE sessionId = :sessionId")
    suspend fun deleteSession(sessionId: String)
}