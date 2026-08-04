package com.example.swiftshare.presentation.activetransfer.ui

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.swiftshare.R
import com.example.swiftshare.base.BaseFragment
import com.example.swiftshare.common.extensions.collectLifecycleFlow
import com.example.swiftshare.common.extensions.setDebouncedClickListener
import com.example.swiftshare.common.extensions.visibleIf
import com.example.swiftshare.databinding.TransferFragmentActiveBinding
import com.example.swiftshare.domain.model.TransferDirection
import com.example.swiftshare.presentation.activetransfer.adapters.FileTransferProgressAdapter
import com.example.swiftshare.presentation.activetransfer.viewmodels.ActiveTransferDetailViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class ActiveTransferDetailFragment :
    BaseFragment<TransferFragmentActiveBinding>(TransferFragmentActiveBinding::inflate) {

    private val viewModel: ActiveTransferDetailViewModel by viewModels()
    private lateinit var adapter: FileTransferProgressAdapter

    override fun setupViews() {
        adapter = FileTransferProgressAdapter()
        binding.rvFiles.adapter = adapter

        binding.btnCancel.setDebouncedClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.active_transfer_cancel_title)
                .setMessage(R.string.active_transfer_cancel_message)
                .setPositiveButton(R.string.active_transfer_cancel_confirm) { _, _ -> viewModel.cancelTransfer() }
                .setNegativeButton(R.string.common_dismiss, null)
                .show()
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

            binding.tvDirection.text = when (session.direction) {
                TransferDirection.SENT -> getString(R.string.active_transfer_sending_to, session.device.displayName)
                TransferDirection.RECEIVED -> getString(R.string.active_transfer_receiving_from, session.device.displayName)
            }

            binding.btnCancel.visibleIf(!state.isComplete)

            if (state.navigateToCompletion) {
                viewModel.consumeNavigateToCompletion()
                val bundle = Bundle().apply { putString("sessionId", session.sessionId) }
                findNavController().navigate(R.id.action_activeTransferDetail_to_completion, bundle)
            }
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