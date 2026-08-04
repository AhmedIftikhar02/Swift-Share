package com.example.swiftshare.presentation.completion.ui

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.swiftshare.R
import com.example.swiftshare.base.BaseFragment
import com.example.swiftshare.common.extensions.collectLifecycleFlow
import com.example.swiftshare.common.extensions.visibleIf
import com.example.swiftshare.databinding.TransferFragmentCompletionBinding
import com.example.swiftshare.domain.model.FileTransferStatus
import com.example.swiftshare.domain.model.TransferDirection
import com.example.swiftshare.presentation.completion.adapters.FailedFileAdapter
import com.example.swiftshare.presentation.completion.viewmodels.CompletionViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CompletionFragment : BaseFragment<TransferFragmentCompletionBinding>(TransferFragmentCompletionBinding::inflate) {

    private val viewModel: CompletionViewModel by viewModels()
    private lateinit var adapter: FailedFileAdapter

    override fun setupViews() {
        adapter = FailedFileAdapter()
        binding.rvFailedFiles.adapter = adapter

        binding.btnDone.setOnClickListener {
            findNavController().navigate(R.id.action_completion_to_discovery)
        }
        binding.btnSendMore.setOnClickListener {
            findNavController().navigate(R.id.action_completion_to_fileQueueReview)
        }
    }

    override fun observeData() {
        viewModel.uiState.collectLifecycleFlow(this) { state ->
            val session = state.session

            if (session == null) {
                binding.groupContent.visibleIf(false)
                binding.tvNotFound.visibleIf(!state.isLoading)
                return@collectLifecycleFlow
            }

            binding.groupContent.visibleIf(true)
            binding.tvNotFound.visibleIf(false)

            val total = session.files.size
            val succeeded = session.files.count { it.status == FileTransferStatus.COMPLETED }
            val failed = session.files.filter {
                it.status == FileTransferStatus.FAILED || it.status == FileTransferStatus.CANCELLED
            }

            binding.ivResultIcon.setImageResource(
                if (failed.isEmpty()) R.drawable.ic_check_circle else R.drawable.ic_error_circle
            )
            binding.tvSummary.text = getString(R.string.completion_summary_format, succeeded, total)

            binding.groupFailed.visibleIf(failed.isNotEmpty())
            adapter.submitList(failed)
            binding.btnSendMore.visibleIf(session.direction == TransferDirection.SENT)
        }
    }
}