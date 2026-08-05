package com.example.swiftshare.domain.model

data class TransferSessionModel(
    val sessionId: String,
    val device: DeviceModel,
    val direction: TransferDirection,
    val startedAt: Long,
    val endedAt: Long?,
    val status: SessionStatus,
    val files: List<FileTransferModel> = emptyList()
)

enum class TransferDirection { SENT, RECEIVED }

enum class SessionStatus {
    CONNECTING, CONNECTED, IN_PROGRESS, PAUSED,
    // Phase 8: distinct from PAUSED so the UI can show "reconnecting…" instead of a plain
    // paused state while a bounded rediscovery/reconnection attempt is in flight (PRD 10.6,
    // PRD 13 "Connection Lost" row).
    RECONNECTING,
    COMPLETED, FAILED, PARTIAL, CANCELLED
}