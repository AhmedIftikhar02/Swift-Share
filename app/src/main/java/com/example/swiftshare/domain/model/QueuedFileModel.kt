package com.example.swiftshare.domain.model

/** A single file staged for sending, before any bytes move (Phase 7 turns a confirmed queue
 *  into actual `FileTransferModel`s). Deliberately separate from `FileTransferModel` — this
 *  model has no transfer progress/status, only selection-time metadata (PRD 2.6). */
data class QueuedFileModel(
    val uri: String,
    val fileName: String,
    val mimeType: String,
    val sizeBytes: Long,
    /** True once SAF metadata (name/size/MIME) has resolved; the row renders a lightweight
     *  placeholder until then so large batches (PRD 2.6 Edge Case, 500+ files) stay responsive. */
    val isMetadataResolved: Boolean = false
)