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
    val errorCode: String? = null,
    // Phase 8: source file's last-modified timestamp at the moment it was queued/sent,
    // used by SourceFileValidator to detect the source changing before a resume/retry
    // re-sends it (PRD 2.11 edge case). 0L means "unknown" (provider didn't report one).
    val sourceLastModified: Long = 0L
)

enum class FileTransferStatus { QUEUED, TRANSFERRING, PAUSED, COMPLETED, FAILED, CANCELLED }