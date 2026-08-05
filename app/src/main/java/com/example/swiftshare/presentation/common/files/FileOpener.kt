package com.example.swiftshare.presentation.common.files

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri

/**
 * Opens a transferred file (sent or received) in whichever app the system/user picks —
 * gallery for images, a PDF reader for PDFs, and so on — via a standard ACTION_VIEW chooser.
 *
 * Works for both:
 *  - Receiver-side files: real `content://media/...` MediaStore URIs written by
 *    ReceivedFileSaver, which are directly openable.
 *  - Sender-side files: the original SAF picker URI captured when the file was queued.
 *
 * FLAG_GRANT_READ_URI_PERMISSION is always attached — required for SAF URIs from another
 * provider, harmless (and unnecessary but safe) for our own MediaStore URIs.
 */
object FileOpener {

    /** Returns true if an app was found and launched, false if there was nothing to open with. */
    fun open(context: Context, uriString: String, mimeType: String): Boolean {
        if (uriString.isBlank()) return false
        val uri = runCatching { Uri.parse(uriString) }.getOrNull() ?: return false
        val safeMimeType = mimeType.ifBlank { "*/*" }

        val viewIntent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, safeMimeType)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        return try {
            context.startActivity(Intent.createChooser(viewIntent, null))
            true
        } catch (e: ActivityNotFoundException) {
            false
        } catch (e: Exception) {
            false
        }
    }
}