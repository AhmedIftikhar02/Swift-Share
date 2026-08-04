package com.example.swiftshare.presentation.activetransfer.adapters

import com.example.swiftshare.R
import com.example.swiftshare.base.BaseAdapter
import com.example.swiftshare.databinding.TransferItemFileProgressBinding
import com.example.swiftshare.domain.model.FileTransferModel
import com.example.swiftshare.domain.model.FileTransferStatus
import java.util.Locale

class FileTransferProgressAdapter : BaseAdapter<FileTransferModel, TransferItemFileProgressBinding>(
    bindingInflater = TransferItemFileProgressBinding::inflate,
    areItemsTheSame = { old, new -> old.fileTransferId == new.fileTransferId },
    areContentsTheSame = { old, new -> old == new }
) {
    override fun bind(binding: TransferItemFileProgressBinding, item: FileTransferModel, position: Int) {
        binding.tvFileName.text = item.fileName

        val percent = if (item.totalBytes > 0) {
            ((item.transferredBytes * 100) / item.totalBytes).toInt().coerceIn(0, 100)
        } else 0
        binding.progressFile.progress = percent

        val context = binding.root.context
        val (statusRes, colorRes, iconRes) = when (item.status) {
            FileTransferStatus.QUEUED -> Triple(R.string.file_status_queued, R.color.color_text_secondary, R.drawable.ic_file_generic)
            FileTransferStatus.TRANSFERRING -> Triple(R.string.file_status_transferring, R.color.color_primary, R.drawable.ic_file_generic)
            FileTransferStatus.PAUSED -> Triple(R.string.file_status_paused, R.color.color_text_secondary, R.drawable.ic_file_generic)
            FileTransferStatus.COMPLETED -> Triple(R.string.file_status_completed, R.color.color_success, R.drawable.ic_check_circle)
            FileTransferStatus.FAILED -> Triple(R.string.file_status_failed, R.color.color_error, R.drawable.ic_error_circle)
            FileTransferStatus.CANCELLED -> Triple(R.string.file_status_cancelled, R.color.color_text_secondary, R.drawable.ic_error_circle)
        }
        binding.tvStatus.setText(statusRes)
        binding.tvStatus.setTextColor(context.getColor(colorRes))
        binding.ivStatusIcon.setImageResource(iconRes)
        binding.tvFileSize.text = context.getString(
            R.string.active_transfer_bytes_format, formatBytes(item.transferredBytes), formatBytes(item.totalBytes)
        )
    }

    private fun formatBytes(bytes: Long): String {
        if (bytes <= 0) return "0 B"
        val units = arrayOf("B", "KB", "MB", "GB")
        val digitGroups = (Math.log10(bytes.toDouble()) / Math.log10(1024.0)).toInt().coerceIn(0, units.lastIndex)
        return String.format(Locale.getDefault(), "%.1f %s", bytes / Math.pow(1024.0, digitGroups.toDouble()), units[digitGroups])
    }
}