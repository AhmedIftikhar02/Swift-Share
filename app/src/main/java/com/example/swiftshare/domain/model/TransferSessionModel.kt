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
    CONNECTING, CONNECTED, IN_PROGRESS, PAUSED, COMPLETED, FAILED, PARTIAL, CANCELLED
}