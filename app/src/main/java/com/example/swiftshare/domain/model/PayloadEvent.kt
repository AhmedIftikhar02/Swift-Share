package com.example.swiftshare.domain.model


sealed class PayloadEvent {

    data class BytesReceived(val endpointId: String, val payloadId: Long, val bytes: ByteArray) : PayloadEvent()

    data class FileIncomingStarted(val endpointId: String, val payloadId: Long) : PayloadEvent()

    data class TransferUpdate(
        val payloadId: Long,
        val bytesTransferred: Long,
        val totalBytes: Long,
        val status: PayloadStatus
    ) : PayloadEvent()
}