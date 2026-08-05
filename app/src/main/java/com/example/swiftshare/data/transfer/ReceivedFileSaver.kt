package com.example.swiftshare.data.transfer

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.example.swiftshare.common.providers.DispatcherProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class ReceivedFileSaver @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dispatcherProvider: DispatcherProvider
) {
    companion object {
        private const val RELATIVE_PATH = "Download/SwiftShare"
    }

    suspend fun saveToDownloads(sourceUri: Uri, displayName: String, mimeType: String): String? =
        withContext(dispatcherProvider.io) {
            val resolver = context.contentResolver
            val safeMimeType = mimeType.ifBlank { "application/octet-stream" }

            val values = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, displayName)
                put(MediaStore.Downloads.MIME_TYPE, safeMimeType)
                put(MediaStore.Downloads.RELATIVE_PATH, RELATIVE_PATH)
                put(MediaStore.Downloads.IS_PENDING, 1)
            }

            val collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            val destinationUri = runCatching { resolver.insert(collection, values) }.getOrNull()

            if (destinationUri == null) {
                Timber.tag("ReceivedFileSaver").w("Could not create MediaStore entry for %s", displayName)
                return@withContext null
            }

            val copied = runCatching {
                resolver.openOutputStream(destinationUri)?.use { out ->
                    resolver.openInputStream(sourceUri)?.use { input -> input.copyTo(out) }
                        ?: throw IllegalStateException("openInputStream(sourceUri) returned null")
                } ?: throw IllegalStateException("openOutputStream(destinationUri) returned null")
            }

            if (copied.isFailure) {
                Timber.tag("ReceivedFileSaver").e(copied.exceptionOrNull(), "Failed writing %s", displayName)
                runCatching { resolver.delete(destinationUri, null, null) }
                return@withContext null
            }

            runCatching {
                values.clear()
                values.put(MediaStore.Downloads.IS_PENDING, 0)
                resolver.update(destinationUri, values, null, null)
            }

            runCatching { resolver.delete(sourceUri, null, null) }

            destinationUri.toString()
        }

    /**
     * Phase 8: deletes the Nearby Connections SDK's internal partial-file cache entry for a
     * payload that failed or was cancelled mid-receive, so it doesn't linger as orphaned
     * storage (PRD 2.12 "Partial files on the receiver are deleted"; PRD 13 "Cancelled
     * Transfer" row). Safe to call even if the underlying file was already cleaned up by
     * the SDK — failures are swallowed since there's nothing further the caller can do.
     */
    suspend fun deleteIncompletePayload(uri: Uri) = withContext(dispatcherProvider.io) {
        runCatching { context.contentResolver.delete(uri, null, null) }
            .onFailure { Timber.tag("ReceivedFileSaver").w(it, "Failed to delete incomplete payload at %s", uri) }
    }
}