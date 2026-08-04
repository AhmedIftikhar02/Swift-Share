package com.example.swiftshare.presentation.completion.adapters


import com.example.swiftshare.base.BaseAdapter
import com.example.swiftshare.databinding.TransferItemFailedFileBinding
import com.example.swiftshare.domain.model.FileTransferModel

class FailedFileAdapter : BaseAdapter<FileTransferModel, TransferItemFailedFileBinding>(
    bindingInflater = TransferItemFailedFileBinding::inflate,
    areItemsTheSame = { old, new -> old.fileTransferId == new.fileTransferId },
    areContentsTheSame = { old, new -> old == new }
) {
    override fun bind(binding: TransferItemFailedFileBinding, item: FileTransferModel, position: Int) {
        binding.tvFileName.text = item.fileName
    }
}