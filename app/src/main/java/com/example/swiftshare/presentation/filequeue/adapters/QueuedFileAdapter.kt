package com.example.swiftshare.presentation.filequeue.adapters


import coil.load
import com.example.swiftshare.R
import com.example.swiftshare.base.BaseAdapter
import com.example.swiftshare.databinding.TransferItemQueuedFileBinding
import com.example.swiftshare.domain.model.QueuedFileModel
import java.util.Locale

class QueuedFileAdapter(
    private val onRemoveClick: (QueuedFileModel) -> Unit
) : BaseAdapter<QueuedFileModel, TransferItemQueuedFileBinding>(
    bindingInflater = TransferItemQueuedFileBinding::inflate,
    areItemsTheSame = { old, new -> old.uri == new.uri },
    areContentsTheSame = { old, new -> old == new }
) {
    override fun bind(binding: TransferItemQueuedFileBinding, item: QueuedFileModel, position: Int) {
        binding.tvFileName.text = item.fileName
        binding.tvFileSize.text = formatBytes(item.sizeBytes)

        if (item.mimeType.startsWith("image/") || item.mimeType.startsWith("video/")) {
            binding.ivThumbnail.load(item.uri) {
                placeholder(R.drawable.ic_star)
                error(R.drawable.ic_star)
                crossfade(true)
            }
        } else {
            binding.ivThumbnail.setImageResource(R.drawable.ic_star)
        }

        binding.btnRemove.setOnClickListener { onRemoveClick(item) }
    }

    private fun formatBytes(bytes: Long): String {
        if (bytes <= 0) return "0 B"
        val units = arrayOf("B", "KB", "MB", "GB")
        val digitGroups = (Math.log10(bytes.toDouble()) / Math.log10(1024.0)).toInt().coerceIn(0, units.lastIndex)
        return String.format(Locale.getDefault(), "%.1f %s", bytes / Math.pow(1024.0, digitGroups.toDouble()), units[digitGroups])
    }
}