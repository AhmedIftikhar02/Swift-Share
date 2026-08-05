package com.example.swiftshare.presentation.completion.adapters

import com.example.swiftshare.base.BaseAdapter
import com.example.swiftshare.common.extensions.setDebouncedClickListener
import com.example.swiftshare.common.extensions.visibleIf
import com.example.swiftshare.databinding.TransferItemFailedFileBinding
import com.example.swiftshare.domain.model.FileTransferModel
import com.example.swiftshare.domain.model.FileTransferStatus

class FailedFileAdapter(
    private val onRetryClick: (FileTransferModel) -> Unit
) : BaseAdapter<FileTransferModel, TransferItemFailedFileBinding>(
    bindingInflater = TransferItemFailedFileBinding::inflate,
    areItemsTheSame = { old, new -> old.fileTransferId == new.fileTransferId },
    areContentsTheSame = { old, new -> old == new }
) {
    override fun bind(binding: TransferItemFailedFileBinding, item: FileTransferModel, position: Int) {
        binding.tvFileName.text = item.fileName
        // Phase 8: retry only makes sense for a file still in a terminal failed/cancelled
        // state — guards against a stray tap racing an in-flight retry re-rendering the row.
        val canRetry = item.status == FileTransferStatus.FAILED || item.status == FileTransferStatus.CANCELLED
        binding.btnRetry.visibleIf(canRetry)
        binding.btnRetry.setDebouncedClickListener { onRetryClick(item) }
    }
}