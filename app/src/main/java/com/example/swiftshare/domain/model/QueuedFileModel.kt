package com.example.swiftshare.domain.model

data class QueuedFileModel(
    val uri: String,
    val fileName: String,
    val mimeType: String,
    val sizeBytes: Long,
    // Phase 8: carried through from FileMetadataResolver so it can be stamped onto the
    // FileTransferModel created for this file once it's actually queued into a session.
    val lastModified: Long = 0L,
    val isMetadataResolved: Boolean = false
)