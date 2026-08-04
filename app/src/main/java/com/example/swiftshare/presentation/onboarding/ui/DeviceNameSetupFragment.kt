package com.example.swiftshare.presentation.onboarding.ui

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.swiftshare.R
import com.example.swiftshare.base.BaseFragment
import com.example.swiftshare.common.extensions.setDebouncedClickListener
import com.example.swiftshare.common.extensions.withPressBounce
import com.example.swiftshare.databinding.FragmentDeviceNameSetupBinding
import com.example.swiftshare.presentation.onboarding.viewmodels.DeviceNameSetupViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeviceNameSetupFragment :
    BaseFragment<FragmentDeviceNameSetupBinding>(FragmentDeviceNameSetupBinding::inflate) {

    private val viewModel: DeviceNameSetupViewModel by viewModels()

    override fun setupViews() {
        binding.etDeviceName.setText(viewModel.defaultDeviceName)
        binding.etDeviceName.setSelection(binding.etDeviceName.text?.length ?: 0)

        binding.btnContinue.withPressBounce()
        binding.btnContinue.setDebouncedClickListener {
            val name = binding.etDeviceName.text?.toString().orEmpty()
            viewModel.completeOnboarding(name)
            runCatching { findNavController().navigate(R.id.action_deviceNameSetup_to_discoveryGraph) }
        }
    }
}