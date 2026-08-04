package com.example.swiftshare.presentation.discovery.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.swiftshare.R
import com.example.swiftshare.base.BaseFragment
import com.example.swiftshare.common.extensions.collectLifecycleFlow
import com.example.swiftshare.databinding.DiscoveryFragmentHomeBinding
import com.example.swiftshare.permissions.PermissionManager
import com.example.swiftshare.presentation.discovery.adapters.DeviceAdapter
import com.example.swiftshare.presentation.discovery.viewmodels.DiscoveryViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import android.util.Log

@AndroidEntryPoint
class DiscoveryFragment : BaseFragment<DiscoveryFragmentHomeBinding>(DiscoveryFragmentHomeBinding::inflate) {

    private val viewModel: DiscoveryViewModel by viewModels()
    @Inject lateinit var permissionManager: PermissionManager

    private lateinit var deviceAdapter: DeviceAdapter

    override fun setupViews() {
        deviceAdapter = DeviceAdapter { device -> onDeviceSelected(device) }

        binding.rvDevices.apply {
            adapter = deviceAdapter
            setHasFixedSize(true)
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        }

        binding.swipeRefresh.setOnRefreshListener {
            Log.d("DiscoveryFragment", "Pull to refresh")
            viewModel.rescan()
        }

        binding.fabPairAlternate.setOnClickListener { showAlternatePairingSheet() }
    }

    override fun observeData() {
        viewModel.uiState.collectLifecycleFlow(this) { state ->
            deviceAdapter.connectingEndpointId = state.connectingEndpointId
            deviceAdapter.submitList(state.devices)
            binding.swipeRefresh.isRefreshing = false

            when {
                state.errorMessage != null -> {
                    Log.d("DiscoveryFragment", "Showing error: ${state.errorMessage}")
                    binding.stateLayout.showError(state.errorMessage) {
                        viewModel.rescan()
                    }
                }
                state.isInitialLoading -> {
                    Log.d("DiscoveryFragment", "Showing loading")
                    binding.stateLayout.showLoading()
                }
                state.devices.isEmpty() -> {
                    Log.d("DiscoveryFragment", "Showing empty state")
                    binding.stateLayout.showEmpty(
                        title = getString(R.string.discovery_empty_title),
                        subtitle = getString(R.string.discovery_empty_subtitle),
                        retryLabel = getString(R.string.discovery_scan_again)
                    ) { viewModel.rescan() }
                }
                else -> {
                    Log.d("DiscoveryFragment", "Showing ${state.devices.size} devices")
                    binding.stateLayout.showContent()
                }
            }

            binding.tvRadarStatus.text = if (state.isScanning) {
                getString(R.string.discovery_scanning)
            } else {
                getString(R.string.discovery_idle)
            }

            state.connectionErrorMessage?.let {
                com.google.android.material.snackbar.Snackbar
                    .make(binding.root, it, com.google.android.material.snackbar.Snackbar.LENGTH_LONG)
                    .show()
                viewModel.consumeConnectionError()
            }

            state.resolvedEndpointId?.let { endpointId ->
                Log.d("DiscoveryFragment", "Navigating to confirmation for: $endpointId")
                val bundle = Bundle().apply { putString("endpointId", endpointId) }
                findNavController().navigate(R.id.action_discovery_to_connectionConfirmation, bundle)
                viewModel.consumeResolvedEndpoint()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("DiscoveryFragment", "onResume")
        if (permissionManager.allCriticalPermissionsGranted()) {
            viewModel.startScanning()
        } else {
            findNavController().navigate(R.id.permissionRationaleFragment)
        }
    }

    override fun onStop() {
        super.onStop()
        Log.d("DiscoveryFragment", "onStop")
        if (!isRemoving && !isDetached) {
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d("DiscoveryFragment", "onPause - NOT stopping scan")
//        viewModel.stopScanning()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("DiscoveryFragment", "onDestroyView - NOT stopping scan")
//        viewModel.stopScanning()
    }

    private fun onDeviceSelected(device: com.example.swiftshare.domain.model.DeviceModel) {
        Log.d("DiscoveryFragment", "Device selected: ${device.displayName} (${device.endpointId})")
        viewModel.onDeviceTapped(device)
    }

    private fun showAlternatePairingSheet() {
        BottomSheetDialog(requireContext()).apply {
            setContentView(R.layout.discovery_sheet_pairing_options)
            findViewById<View>(R.id.optionQr)?.setOnClickListener {
                dismiss()
                findNavController().navigate(R.id.action_discovery_to_qrPairing)
            }
            findViewById<View>(R.id.optionPin)?.setOnClickListener {
                dismiss()
                findNavController().navigate(R.id.action_discovery_to_pinPairing)
            }
        }.show()
    }
}