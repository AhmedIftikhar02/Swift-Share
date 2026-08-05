package com.example.swiftshare.presentation.activetransfer.ui

import androidx.fragment.app.viewModels
import com.example.swiftshare.R
import com.example.swiftshare.base.BaseFragment
import com.example.swiftshare.base.UiEvent
import com.example.swiftshare.common.extensions.collectLifecycleFlow
import com.example.swiftshare.common.extensions.setDebouncedClickListener
import com.example.swiftshare.common.extensions.toast
import com.example.swiftshare.common.extensions.visibleIf
import com.example.swiftshare.databinding.TransferFragmentActiveBinding
import com.example.swiftshare.domain.model.FileTransferStatus
import com.example.swiftshare.domain.model.TransferDirection
import com.example.swiftshare.presentation.activetransfer.adapters.FileTransferProgressAdapter
import com.example.swiftshare.presentation.activetransfer.viewmodels.ActiveTransferDetailViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import androidx.recyclerview.widget.LinearLayoutManager
import java.util.Locale

@AndroidEntryPoint
class ActiveTransferDetailFragment :
    BaseFragment<TransferFragmentActiveBinding>(TransferFragmentActiveBinding::inflate) {

    private val viewModel: ActiveTransferDetailViewModel by viewModels()
    private lateinit var adapter: FileTransferProgressAdapter

    override fun setupViews() {
        adapter = FileTransferProgressAdapter()
        binding.rvFiles.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFiles.adapter = adapter

        binding.btnCancel.setDebouncedClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.active_transfer_cancel_title)
                .setMessage(R.string.active_transfer_cancel_message)
                .setPositiveButton(R.string.active_transfer_cancel_confirm) { _, _ -> viewModel.cancelTransfer() }
                .setNegativeButton(R.string.common_dismiss, null)
                .show()
        }

        binding.btnPauseResume.setDebouncedClickListener {
            viewModel.togglePauseResume()
        }
    }

    override fun observeData() {
        viewModel.uiState.collectLifecycleFlow(this) { state ->
            val session = state.session ?: return@collectLifecycleFlow

            adapter.submitList(session.files)

            binding.progressOverall.progress = state.overallProgressPercent
            binding.tvOverallPercent.text = getString(R.string.active_transfer_percent_format, state.overallProgressPercent)
            binding.tvOverallBytes.text = getString(
                R.string.active_transfer_bytes_format, formatBytes(state.transferredBytes), formatBytes(state.totalBytes)
            )
            binding.tvSpeed.text = if (state.speedBytesPerSecond > 0) {
                getString(R.string.active_transfer_speed_format, formatBytes(state.speedBytesPerSecond))
            } else {
                getString(R.string.active_transfer_speed_calculating)
            }
            binding.tvEta.text = state.etaSeconds?.let(::formatEta).orEmpty()

            val isSent = session.direction == TransferDirection.SENT
            binding.tvDirection.text = if (isSent) {
                getString(R.string.active_transfer_sending_to, session.device.displayName)
            } else {
                getString(R.string.active_transfer_receiving_from, session.device.displayName)
            }
            binding.ivDirectionIcon.setImageResource(
                if (isSent) R.drawable.ic_arrow_upload else R.drawable.ic_arrow_download
            )

            val doneCount = session.files.count { it.status == FileTransferStatus.COMPLETED }
            binding.tvFileCount.text = getString(
                R.string.active_transfer_file_count_format, doneCount, session.files.size
            )

            binding.tvStatusBanner.visibleIf(state.isPaused || state.isReconnecting)
            binding.tvStatusBanner.text = when {
                state.isReconnecting -> getString(R.string.active_transfer_reconnecting)
                state.isPaused -> getString(R.string.active_transfer_paused_banner)
                else -> ""
            }

            binding.btnCancel.visibleIf(!state.isComplete)

            binding.btnPauseResume.visibleIf(state.canPauseResume)
            binding.btnPauseResume.isEnabled = !state.isReconnecting
            binding.btnPauseResume.text = if (state.isPaused || state.isReconnecting) {
                getString(R.string.active_transfer_resume)
            } else {
                getString(R.string.active_transfer_pause)
            }
        }

        viewModel.uiEvent.collectLifecycleFlow(this) { event ->
            if (event is UiEvent.ShowError) toast(event.exception.message)
        }
    }

    private fun formatBytes(bytes: Long): String {
        if (bytes <= 0) return "0 B"
        val units = arrayOf("B", "KB", "MB", "GB")
        val digitGroups = (Math.log10(bytes.toDouble()) / Math.log10(1024.0)).toInt().coerceIn(0, units.lastIndex)
        return String.format(Locale.getDefault(), "%.1f %s", bytes / Math.pow(1024.0, digitGroups.toDouble()), units[digitGroups])
    }

    private fun formatEta(seconds: Long): String {
        val minutes = seconds / 60
        val secs = seconds % 60
        return if (minutes > 0) getString(R.string.active_transfer_eta_minutes, minutes, secs)
        else getString(R.string.active_transfer_eta_seconds, secs)
    }
}