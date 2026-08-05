package com.example.swiftshare.data.transfer

import android.content.ContentResolver
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.OpenableColumns
import com.example.swiftshare.common.providers.DispatcherProvider
import com.example.swiftshare.domain.model.FileTransferModel
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Re-checks a paused/failed file's source [Uri] before a resume or retry actually re-sends
 * bytes, per PRD 2.11's "source file changed since pausing" edge case. Shared by both
 * [com.example.swiftshare.data.repository.TransferRepositoryImpl.resumeTransfer] and
 * `retryFile` so the check lives in exactly one place.
 */
class SourceFileValidator @Inject constructor(
    private val contentResolver: ContentResolver,
    private val dispatcherProvider: DispatcherProvider
) {
    sealed class ValidationResult {
        data object Valid : ValidationResult()
        data object MissingOrInaccessible : ValidationResult()
        data object Changed : ValidationResult()
    }

    suspend fun validate(file: FileTransferModel): ValidationResult = withContext(dispatcherProvider.io) {
        val uri = runCatching { Uri.parse(file.uri) }.getOrNull()
            ?: return@withContext ValidationResult.MissingOrInaccessible

        val accessible = runCatching {
            contentResolver.openFileDescriptor(uri, "r")?.use { true } ?: false
        }.getOrDefault(false)
        if (!accessible) return@withContext ValidationResult.MissingOrInaccessible

        var currentSize = -1L
        var currentModified = 0L
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                val modifiedIndex = cursor.getColumnIndex(DocumentsContract.Document.COLUMN_LAST_MODIFIED)
                if (sizeIndex >= 0 && !cursor.isNull(sizeIndex)) currentSize = cursor.getLong(sizeIndex)
                if (modifiedIndex >= 0 && !cursor.isNull(modifiedIndex)) currentModified = cursor.getLong(modifiedIndex)
            }
        }

        val sizeChanged = currentSize >= 0 && currentSize != file.totalBytes
        // last-modified isn't guaranteed by every provider — only trust it when both the
        // recorded and current readings are non-zero, otherwise size is the sole signal.
        val modifiedChanged = file.sourceLastModified > 0L &&
                currentModified > 0L &&
                currentModified != file.sourceLastModified

        if (sizeChanged || modifiedChanged) ValidationResult.Changed else ValidationResult.Valid
    }
}