package com.example.swiftshare.data.transfer

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import com.example.swiftshare.common.providers.DispatcherProvider
import com.example.swiftshare.domain.model.QueuedFileModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import javax.inject.Inject


class FileMetadataResolver @Inject constructor(
    private val contentResolver: ContentResolver,
    private val dispatcherProvider: DispatcherProvider
) {
    private val concurrencyLimit = Semaphore(permits = 6)

    suspend fun resolveOne(rawUri: String): QueuedFileModel? = withContext(dispatcherProvider.io) {  // Removed parentheses
        concurrencyLimit.withPermit {
            runCatching {
                val uri = Uri.parse(rawUri)
                var name = uri.lastPathSegment ?: "file"
                var size = 0L

                contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                        if (nameIndex >= 0) name = cursor.getString(nameIndex) ?: name
                        if (sizeIndex >= 0 && !cursor.isNull(sizeIndex)) size = cursor.getLong(sizeIndex)
                    }
                }

                contentResolver.openFileDescriptor(uri, "r")?.use { } ?: return@withContext null

                val mimeType = contentResolver.getType(uri) ?: "application/octet-stream"

                QueuedFileModel(
                    uri = rawUri,
                    fileName = name,
                    mimeType = mimeType,
                    sizeBytes = size,
                    isMetadataResolved = true
                )
            }.getOrNull()
        }
    }

    suspend fun resolveMany(rawUris: List<String>): List<QueuedFileModel?> = coroutineScope {
        rawUris.map { uri -> async { resolveOne(uri) } }.map { it.await() }
    }
}