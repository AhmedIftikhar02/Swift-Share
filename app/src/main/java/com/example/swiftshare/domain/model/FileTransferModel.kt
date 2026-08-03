package com.example.swiftshare.domain.model

data class FileTransferModel(
    val fileTransferId: String,
    val sessionId: String,
    val fileName: String,
    val mimeType: String,
    val totalBytes: Long,
    val transferredBytes: Long,
    val uri: String,
    val status: FileTransferStatus,
    val checksum: String? = null,
    val errorCode: String? = null
)

enum class FileTransferStatus { QUEUED, TRANSFERRING, PAUSED, COMPLETED, FAILED, CANCELLED }