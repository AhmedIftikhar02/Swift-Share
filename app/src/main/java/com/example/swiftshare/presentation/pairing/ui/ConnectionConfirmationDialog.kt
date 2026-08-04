package com.example.swiftshare.presentation.pairing.ui

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.swiftshare.R
import com.example.swiftshare.base.BaseDialog
import com.example.swiftshare.common.extensions.collectLifecycleFlow
import com.example.swiftshare.common.extensions.cleanDeviceName  // Import the extension
import com.example.swiftshare.databinding.PairingDialogConfirmationBinding
import com.example.swiftshare.presentation.pairing.viewmodels.ConnectionConfirmationViewModel
import dagger.hilt.android.AndroidEntryPoint
import android.util.Log

@AndroidEntryPoint
class ConnectionConfirmationDialog :
    BaseDialog<PairingDialogConfirmationBinding>(PairingDialogConfirmationBinding::inflate) {

    private val viewModel: ConnectionConfirmationViewModel by viewModels()

    private var endpointId: String = ""
    private var deviceName: String = ""

    override fun setupViews() {
        isCancelable = false

        endpointId = arguments?.getString("endpointId").orEmpty()
        Log.d("ConfirmationDialog", "Dialog opened with endpointId: $endpointId")

        if (endpointId.isBlank()) {
            Log.e("ConfirmationDialog", "No endpointId provided, dismissing")
            dismiss()
            return
        }

        // Call observe BEFORE collecting flow
        viewModel.observe(endpointId)

        binding.btnAccept.setOnClickListener { viewModel.accept() }
        binding.btnReject.setOnClickListener { viewModel.reject() }

        dialog?.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == android.view.KeyEvent.KEYCODE_BACK) {
                viewModel.reject()
                true
            } else false
        }
    }

    override fun observeData() {
        viewModel.uiState.collectLifecycleFlow(this) { state ->
            Log.d("ConfirmationDialog", "UI State: isLoading=${state.isLoading}, digits=${state.authenticationDigits}, isResolved=${state.isResolved}")

            if (state.remoteDeviceName.isNotBlank() && deviceName.isEmpty()) {
                deviceName = state.remoteDeviceName.cleanDeviceName()
                Log.d("ConfirmationDialog", "Cleaned device name: $deviceName")
            }

            // Show/hide loading
            binding.progressBar.visibility = if (state.isLoading) android.view.View.VISIBLE else android.view.View.GONE

            val displayName = if (state.remoteDeviceName.isNotBlank()) {
                state.remoteDeviceName.cleanDeviceName()
            } else {
                getString(R.string.pairing_waiting_for_device)
            }
            binding.tvRemoteDeviceName.text = displayName

            binding.tvAuthToken.text = state.authenticationDigits.ifBlank { "---" }
            binding.tvCountdown.text = getString(R.string.pairing_seconds_remaining, state.secondsRemaining)

            val hasDigits = state.authenticationDigits.isNotBlank()
            val canAccept = hasDigits && !state.isResolved
            binding.btnAccept.isEnabled = canAccept
            binding.btnReject.isEnabled = !state.isResolved

            state.errorMessage?.let {
                com.google.android.material.snackbar.Snackbar
                    .make(binding.root, it, com.google.android.material.snackbar.Snackbar.LENGTH_LONG)
                    .show()
            }

            if (state.isResolved) {
                if (state.isAccepted) {
                    Log.d("ConfirmationDialog", "Connection accepted, navigating to Transfer Hub")
                    val bundle = Bundle().apply {
                        putString("endpointId", endpointId)
                        putString("deviceName", deviceName)
                    }
                    findNavController().navigate(R.id.action_connectionConfirmation_to_transferHub, bundle)
                } else {
                    Log.d("ConfirmationDialog", "Connection rejected or failed, dismissing")
                    dismiss()
                }
            }
        }
    }
}