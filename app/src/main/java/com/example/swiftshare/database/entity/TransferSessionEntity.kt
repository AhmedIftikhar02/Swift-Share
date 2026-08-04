package com.example.swiftshare.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transfer_sessions")
data class TransferSessionEntity(
    @PrimaryKey val sessionId: String,
    val deviceEndpointId: String,
    val deviceName: String,
    val direction: String,
    val startedAt: Long,
    val endedAt: Long?,
    val status: String
)