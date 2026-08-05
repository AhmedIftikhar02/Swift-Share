package com.example.swiftshare.presentation.completion.ui

import android.view.animation.OvershootInterpolator
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.swiftshare.R
import com.example.swiftshare.base.BaseFragment
import com.example.swiftshare.base.UiEvent
import com.example.swiftshare.common.extensions.collectLifecycleFlow
import com.example.swiftshare.common.extensions.toast
import com.example.swiftshare.common.extensions.visibleIf
import com.example.swiftshare.databinding.TransferFragmentCompletionBinding
import com.example.swiftshare.domain.model.FileTransferStatus
import com.example.swiftshare.domain.model.SessionStatus
import com.example.swiftshare.domain.model.TransferDirection
import com.example.swiftshare.presentation.completion.adapters.FailedFileAdapter
import com.example.swiftshare.presentation.completion.adapters.TransferredFileAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.swiftshare.presentation.completion.viewmodels.CompletionViewModel
import com.example.swiftshare.presentation.common.files.FileOpener
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class CompletionFragment : BaseFragment<TransferFragmentCompletionBinding>(TransferFragmentCompletionBinding::inflate) {

    private val viewModel: CompletionViewModel by viewModels()
    private lateinit var failedAdapter: FailedFileAdapter
    private lateinit var transferredAdapter: TransferredFileAdapter
    private var heroAnimationPlayed = false

    override fun setupViews() {
        failedAdapter = FailedFileAdapter(onRetryClick = { file -> viewModel.retryFile(file.fileTransferId) })
        binding.rvFailedFiles.layoutManager = LinearLayoutManager(requireContext())  // Add this
        binding.rvFailedFiles.adapter = failedAdapter

        transferredAdapter = TransferredFileAdapter(onFileClick = { file ->
            val opened = FileOpener.open(requireContext(), file.uri, file.mimeType)
            if (!opened) toast(getString(R.string.completion_open_failed))
        })
        binding.rvTransferredFiles.layoutManager = LinearLayoutManager(requireContext())  // Add this
        binding.rvTransferredFiles.adapter = transferredAdapter

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
            val totalBytes = session.files.sumOf { it.totalBytes }
            val elapsedMs = ((session.endedAt ?: System.currentTimeMillis()) - session.startedAt).coerceAtLeast(0L)
            val isSent = session.direction == TransferDirection.SENT

            val (resultIconRes, resultColorRes, titleRes) = when (session.status) {
                SessionStatus.COMPLETED -> Triple(
                    R.drawable.ic_check_circle, R.color.color_success,
                    if (isSent) R.string.completion_title_all_sent else R.string.completion_title_all_received
                )
                SessionStatus.PARTIAL -> Triple(
                    R.drawable.ic_error_circle, R.color.color_warning,
                    if (isSent) R.string.completion_title_partial_sent else R.string.completion_title_partial_received
                )
                SessionStatus.CANCELLED -> Triple(
                    R.drawable.ic_error_circle, R.color.color_text_secondary, R.string.completion_title_cancelled
                )
                else -> Triple(
                    R.drawable.ic_error_circle, R.color.color_error,
                    if (isSent) R.string.completion_title_failed_sent else R.string.completion_title_failed_received
                )
            }

            binding.ivResultIcon.setImageResource(resultIconRes)
            binding.ivResultIcon.setColorFilter(requireContext().getColor(resultColorRes))
            binding.tvResultTitle.setText(titleRes)
            binding.tvSummary.text = getString(
                if (isSent) R.string.completion_subtitle_sent_format else R.string.completion_subtitle_received_format,
                session.device.displayName
            )

            binding.tvStatFiles.text = getString(R.string.completion_stat_files_format, succeeded, total)
            binding.tvStatSize.text = formatBytes(totalBytes)
            binding.tvStatTime.text = formatDuration(elapsedMs)

            transferredAdapter.submitList(session.files)

            binding.groupFailed.visibleIf(failed.isNotEmpty())
            failedAdapter.submitList(failed)
            binding.btnSendMore.visibleIf(isSent)

            playHeroEntranceAnimationOnce()

            if (state.navigateToActiveTransfer) {
                viewModel.consumeNavigateToActiveTransfer()
                findNavController().navigate(R.id.action_completion_to_activeTransferDetail)
            }
        }

        viewModel.uiEvent.collectLifecycleFlow(this) { event ->
            if (event is UiEvent.ShowError) toast(event.exception.message)
        }
    }

    private fun playHeroEntranceAnimationOnce() {
        if (heroAnimationPlayed) return
        heroAnimationPlayed = true
        binding.ivResultIconBg.apply {
            scaleX = 0.4f
            scaleY = 0.4f
            alpha = 0f
            animate()
                .scaleX(1f)
                .scaleY(1f)
                .alpha(1f)
                .setDuration(420L)
                .setInterpolator(OvershootInterpolator(2f))
                .start()
        }
    }

    private fun formatBytes(bytes: Long): String {
        if (bytes <= 0) return "0 B"
        val units = arrayOf("B", "KB", "MB", "GB")
        val digitGroups = (Math.log10(bytes.toDouble()) / Math.log10(1024.0)).toInt().coerceIn(0, units.lastIndex)
        return String.format(Locale.getDefault(), "%.1f %s", bytes / Math.pow(1024.0, digitGroups.toDouble()), units[digitGroups])
    }

    private fun formatDuration(millis: Long): String {
        val totalSeconds = millis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return if (minutes > 0) getString(R.string.completion_time_minutes_format, minutes, seconds)
        else getString(R.string.completion_time_seconds_format, seconds)
    }
}