package com.example.swiftshare.presentation.pairing.ui

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.swiftshare.R
import com.example.swiftshare.base.BaseFragment
import com.example.swiftshare.common.extensions.collectLifecycleFlow
import com.example.swiftshare.databinding.PairingFragmentPinBinding
import com.example.swiftshare.presentation.pairing.viewmodels.PinPairingViewModel
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PinPairingFragment : BaseFragment<PairingFragmentPinBinding>(PairingFragmentPinBinding::inflate) {

    private val viewModel: PinPairingViewModel by viewModels()

    override fun setupViews() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) = showTab(tab.position)
            override fun onTabUnselected(tab: TabLayout.Tab) = Unit
            override fun onTabReselected(tab: TabLayout.Tab) = Unit
        })
        showTab(0)

        viewModel.generatePin()
        binding.btnRegeneratePin.setOnClickListener { viewModel.regeneratePin() }

        binding.etPinInput.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.onPinInputChanged(s?.toString().orEmpty())
            }
            override fun afterTextChanged(s: android.text.Editable?) = Unit
        })
        binding.btnSubmitPin.setOnClickListener { viewModel.submitPin() }
    }

    override fun observeData() {
        viewModel.uiState.collectLifecycleFlow(this) { state ->
            binding.tvMyPin.text = state.myPin.chunked(3).joinToString(" ")
            binding.tvPinCountdown.text = getString(R.string.pairing_seconds_remaining, state.secondsRemaining)
            binding.groupPinExpired.visibility = if (state.isExpired) android.view.View.VISIBLE else android.view.View.GONE

            binding.btnSubmitPin.isEnabled = state.enteredPin.length == 6 && !state.isSubmitting
            binding.progressPinSubmit.visibility = if (state.isSubmitting) android.view.View.VISIBLE else android.view.View.GONE
            binding.tilPinInput.error = state.errorMessage

            state.resolvedEndpointId?.let { endpointId ->
                val bundle = Bundle().apply { putString("endpointId", endpointId) }
                findNavController().navigate(R.id.action_pinPairing_to_connectionConfirmation, bundle)
                // BUGFIX (Phase 5): consume immediately — see QrPairingFragment for why.
                viewModel.consumeResolvedEndpoint()
            }
        }
    }

    private fun showTab(position: Int) {
        binding.layoutMyPin.visibility = if (position == 0) android.view.View.VISIBLE else android.view.View.GONE
        binding.layoutEnterPin.visibility = if (position == 1) android.view.View.VISIBLE else android.view.View.GONE
    }
}