package com.example.swiftshare.presentation.filequeue.ui

import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.swiftshare.R
import com.example.swiftshare.base.BaseFragment
import com.example.swiftshare.base.UiEvent
import com.example.swiftshare.common.extensions.collectLifecycleFlow
import com.example.swiftshare.common.extensions.toast
import com.example.swiftshare.databinding.TransferFragmentQueueBinding
import com.example.swiftshare.presentation.filequeue.adapters.QueuedFileAdapter
import com.example.swiftshare.presentation.filequeue.viewmodels.FileQueueReviewViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import android.util.Log

@AndroidEntryPoint
class FileQueueReviewFragment : BaseFragment<TransferFragmentQueueBinding>(TransferFragmentQueueBinding::inflate) {

    private val viewModel: FileQueueReviewViewModel by viewModels()
    private lateinit var adapter: QueuedFileAdapter
    private var endpointId: String = ""

    private val filePicker = registerForActivityResult(
        ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        if (uris.isNotEmpty()) {
            Log.d("FileQueueReview", "Adding ${uris.size} more files")
            uris.forEach { uri ->
                runCatching {
                    requireContext().contentResolver.takePersistableUriPermission(
                        uri, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                }
            }
            viewModel.addFiles(endpointId, uris.map { it.toString() })
        }
    }

    override fun setupViews() {
        // Get endpoint ID from arguments (from your version)
        endpointId = arguments?.getString("endpointId").orEmpty()
        Log.d("FileQueueReview", "Endpoint ID: $endpointId")

        if (endpointId.isBlank()) {
            Log.e("FileQueueReview", "No endpoint ID provided!")
            findNavController().popBackStack()
            return
        }

        // Setup adapter (from your version with layout manager)
        adapter = QueuedFileAdapter { file -> viewModel.removeFile(file.uri) }
        binding.rvQueuedFiles.apply {
            adapter = this@FileQueueReviewFragment.adapter
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
            setHasFixedSize(true)
        }

        binding.btnAddMore.setOnClickListener {
            filePicker.launch(arrayOf("*/*"))
        }

        binding.btnSend.setOnClickListener {
            Log.d("FileQueueReview", "Send button clicked")
            binding.btnSend.isEnabled = false
            viewModel.sendFiles()
        }
    }

    override fun observeData() {
        viewModel.uiState.collectLifecycleFlow(this) { state ->
            Log.d("FileQueueReview", "UI State: ${state.files.size} files, isEmpty=${state.isEmpty}, isSending=${state.isSending}")

            adapter.submitList(state.files)

            binding.tvSummary.text = if (state.isEmpty) {
                getString(R.string.queue_empty_summary)
            } else {
                getString(
                    R.string.queue_summary_format,
                    state.files.size,
                    formatBytes(state.totalSizeBytes)
                )
            }

            binding.emptyState.root.visibility = if (state.isEmpty) android.view.View.VISIBLE else android.view.View.GONE
            binding.rvQueuedFiles.visibility = if (state.isEmpty) android.view.View.GONE else android.view.View.VISIBLE

            // Enable Send button only when queue is not empty AND not sending
            binding.btnSend.isEnabled = !state.isEmpty && !state.isSending
            Log.d("FileQueueReview", "Send button enabled: ${!state.isEmpty && !state.isSending}")
        }

        viewModel.navigateToSessionId.collectLifecycleFlow(this) { sessionId ->
            if (sessionId != null) {
                Log.d("FileQueueReview", "Navigating to ActiveTransferDetail with sessionId: $sessionId")
                viewModel.consumeNavigation()
                val bundle = Bundle().apply { putString("sessionId", sessionId) }
                findNavController().navigate(R.id.action_fileQueueReview_to_activeTransferDetail, bundle)
            }
        }

        viewModel.uiEvent.collectLifecycleFlow(this) { event ->
            if (event is UiEvent.ShowError) {
                Log.e("FileQueueReview", "Error: ${event.exception.message}")
                // Re-enable send button after a failed send
                binding.btnSend.isEnabled = viewModel.uiState.value.files.isNotEmpty() && !viewModel.uiState.value.isSending
                toast(event.exception.message)
            }
        }
    }

    private fun endpointId(): String = arguments?.getString("endpointId").orEmpty()

    private fun formatBytes(bytes: Long): String {
        if (bytes <= 0) return "0 B"
        val units = arrayOf("B", "KB", "MB", "GB")
        val digitGroups = (Math.log10(bytes.toDouble()) / Math.log10(1024.0)).toInt().coerceIn(0, units.lastIndex)
        return String.format(Locale.getDefault(), "%.1f %s", bytes / Math.pow(1024.0, digitGroups.toDouble()), units[digitGroups])
    }
}