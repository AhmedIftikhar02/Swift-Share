package com.example.swiftshare.domain.model


data class TransferProgress(
    val fileTransferId: String,
    val bytesTransferred: Long,
    val totalBytes: Long,
    val status: PayloadStatus,
    val fileName: String = "",
    val mimeType: String = "",
    val savedUri: String? = null
)