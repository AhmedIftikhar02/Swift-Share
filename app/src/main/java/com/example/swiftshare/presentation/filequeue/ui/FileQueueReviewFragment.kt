package com.example.swiftshare.presentation.filequeue.ui

import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.swiftshare.R
import com.example.swiftshare.base.BaseFragment
import com.example.swiftshare.common.extensions.collectLifecycleFlow
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
        // Get endpoint ID from arguments
        endpointId = arguments?.getString("endpointId").orEmpty()
        Log.d("FileQueueReview", "Endpoint ID: $endpointId")

        if (endpointId.isBlank()) {
            Log.e("FileQueueReview", "No endpoint ID provided!")
            // Show error and go back
            findNavController().popBackStack()
            return
        }

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
            // Navigate to Active Transfer Detail
            findNavController().navigate(R.id.action_fileQueueReview_to_activeTransferDetail)
        }
    }

    override fun observeData() {
        viewModel.uiState.collectLifecycleFlow(this) { state ->
            Log.d("FileQueueReview", "UI State: ${state.files.size} files, isEmpty=${state.isEmpty}")

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

            // Enable Send button only when queue is not empty
            binding.btnSend.isEnabled = !state.isEmpty
            Log.d("FileQueueReview", "Send button enabled: ${!state.isEmpty}")
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