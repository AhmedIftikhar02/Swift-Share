package com.example.swiftshare.presentation.completion.adapters

import com.example.swiftshare.R
import com.example.swiftshare.base.BaseAdapter
import com.example.swiftshare.common.extensions.setDebouncedClickListener
import com.example.swiftshare.common.extensions.visibleIf
import com.example.swiftshare.databinding.TransferItemTransferredFileBinding
import com.example.swiftshare.domain.model.FileTransferModel
import com.example.swiftshare.domain.model.FileTransferStatus
import com.example.swiftshare.presentation.common.files.FileTypeIconResolver
import java.util.Locale

class TransferredFileAdapter(
    private val onFileClick: (FileTransferModel) -> Unit
) : BaseAdapter<FileTransferModel, TransferItemTransferredFileBinding>(
    bindingInflater = TransferItemTransferredFileBinding::inflate,
    areItemsTheSame = { old, new -> old.fileTransferId == new.fileTransferId },
    areContentsTheSame = { old, new -> old == new }
) {
    override fun bind(binding: TransferItemTransferredFileBinding, item: FileTransferModel, position: Int) {
        val context = binding.root.context

        binding.tvFileName.text = item.fileName
        binding.ivFileIcon.setImageResource(FileTypeIconResolver.iconFor(item.mimeType))

        binding.tvFileMeta.text = context.getString(
            R.string.completion_file_meta_format,
            formatBytes(item.totalBytes),
            extensionOf(item.fileName)
        )

        val (statusIconRes, statusColorRes) = when (item.status) {
            FileTransferStatus.COMPLETED -> R.drawable.ic_check_circle to R.color.color_success
            FileTransferStatus.FAILED, FileTransferStatus.CANCELLED -> R.drawable.ic_error_circle to R.color.color_error
            else -> R.drawable.ic_file_generic to R.color.color_text_secondary
        }
        binding.ivStatusBadge.setImageResource(statusIconRes)
        binding.ivStatusBadge.setColorFilter(context.getColor(statusColorRes))

        val openable = item.status == FileTransferStatus.COMPLETED && item.uri.isNotBlank()
        binding.ivOpenChevron.visibleIf(openable)
        binding.root.isClickable = openable
        binding.root.isFocusable = openable
        binding.root.setDebouncedClickListener {
            if (openable) onFileClick(item)
        }
    }

    private fun formatBytes(bytes: Long): String {
        if (bytes <= 0) return "0 B"
        val units = arrayOf("B", "KB", "MB", "GB")
        val digitGroups = (Math.log10(bytes.toDouble()) / Math.log10(1024.0)).toInt().coerceIn(0, units.lastIndex)
        return String.format(Locale.getDefault(), "%.1f %s", bytes / Math.pow(1024.0, digitGroups.toDouble()), units[digitGroups])
    }

    private fun extensionOf(fileName: String): String =
        fileName.substringAfterLast('.', "").uppercase(Locale.getDefault()).ifBlank { "FILE" }
}