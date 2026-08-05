package com.example.swiftshare.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "file_transfers",
    foreignKeys = [ForeignKey(
        entity = TransferSessionEntity::class,
        parentColumns = ["sessionId"],
        childColumns = ["sessionId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("sessionId")]
)
data class FileTransferEntity(
    @PrimaryKey val fileTransferId: String,
    val sessionId: String,
    val fileName: String,
    val mimeType: String,
    val totalBytes: Long,
    val transferredBytes: Long,
    val uri: String,
    val status: String,
    val checksum: String?,
    val errorCode: String?,
    // Phase 8 — added via MIGRATION_1_2, see AppDatabase.kt.
    val sourceLastModified: Long = 0L
)