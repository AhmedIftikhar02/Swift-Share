package com.example.swiftshare.presentation.common.files

import androidx.annotation.DrawableRes
import com.example.swiftshare.R

/**
 * Maps a MIME type to a representative file-type icon for list rows (Completion screen,
 * and anywhere else a received/sent file needs a quick visual identity).
 */
object FileTypeIconResolver {

    @DrawableRes
    fun iconFor(mimeType: String): Int {
        val type = mimeType.lowercase()
        return when {
            type.startsWith("image/") -> R.drawable.ic_filetype_image
            type.startsWith("video/") -> R.drawable.ic_filetype_video
            type.startsWith("audio/") -> R.drawable.ic_filetype_audio
            type == "application/pdf" -> R.drawable.ic_filetype_document
            type.startsWith("text/") ||
                    type.contains("document") ||
                    type.contains("msword") ||
                    type.contains("wordprocessingml") ||
                    type.contains("spreadsheet") ||
                    type.contains("presentation") -> R.drawable.ic_filetype_document
            type.contains("zip") ||
                    type.contains("rar") ||
                    type.contains("7z") ||
                    type.contains("compressed") ||
                    type.contains("archive") -> R.drawable.ic_filetype_archive
            else -> R.drawable.ic_file_generic
        }
    }
}