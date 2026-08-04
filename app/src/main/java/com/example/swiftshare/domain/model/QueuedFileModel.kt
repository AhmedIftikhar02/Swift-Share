package com.example.swiftshare.domain.model

data class QueuedFileModel(
    val uri: String,
    val fileName: String,
    val mimeType: String,
    val sizeBytes: Long,
    val isMetadataResolved: Boolean = false
)