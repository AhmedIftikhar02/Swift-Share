package com.example.swiftshare.core.nearby

object FileMetadataCodec {
    private const val SEPARATOR = "\u0001"

    fun encode(meta: TransferFileMetadata): ByteArray {
        val safeName = meta.fileName.replace(SEPARATOR, "_")
        return listOf(meta.sessionId, meta.fileTransferId, safeName, meta.mimeType, meta.sizeBytes.toString())
            .joinToString(SEPARATOR)
            .toByteArray(Charsets.UTF_8)
    }


    fun decode(bytes: ByteArray): TransferFileMetadata? = runCatching {
        val parts = String(bytes, Charsets.UTF_8).split(SEPARATOR)
        if (parts.size < 5) return null
        TransferFileMetadata(
            sessionId = parts[0],
            fileTransferId = parts[1],
            fileName = parts[2].ifBlank { "received_file" },
            mimeType = parts[3].ifBlank { "application/octet-stream" },
            sizeBytes = parts[4].toLongOrNull() ?: 0L
        )
    }.getOrNull()
}

data class TransferFileMetadata(
    val sessionId: String,
    val fileTransferId: String,
    val fileName: String,
    val mimeType: String,
    val sizeBytes: Long
)