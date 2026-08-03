package com.example.swiftshare.presentation.transferhub.ui

import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.swiftshare.R
import com.example.swiftshare.base.BaseFragment
import com.example.swiftshare.common.extensions.collectLifecycleFlow
import com.example.swiftshare.databinding.TransferFragmentHubBinding
import com.example.swiftshare.presentation.transferhub.viewmodels.TransferHubViewModel
import dagger.hilt.android.AndroidEntryPoint
import android.util.Log

@AndroidEntryPoint
class TransferHubFragment : BaseFragment<TransferFragmentHubBinding>(TransferFragmentHubBinding::inflate) {

    private val viewModel: TransferHubViewModel by viewModels()
    private var endpointId: String = ""
    private var deviceName: String = ""

    private val filePicker = registerForActivityResult(
        ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        if (uris.isNotEmpty()) {
            Log.d("TransferHub", "Selected ${uris.size} files")
            uris.forEach { uri ->
                runCatching {
                    requireContext().contentResolver.takePersistableUriPermission(
                        uri, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                }
            }
            // Store endpoint ID before navigation
            viewModel.stageFiles(endpointId, uris.map { it.toString() })
        }
    }

    override fun setupViews() {
        // Get endpoint ID and device name from arguments
        endpointId = arguments?.getString("endpointId").orEmpty()
        deviceName = arguments?.getString("deviceName").orEmpty()
        Log.d("TransferHub", "Endpoint ID: $endpointId, Device Name: $deviceName")

        if (endpointId.isBlank()) {
            Log.e("TransferHub", "No endpoint ID provided!")
            // Try to get it from the ViewModel
            endpointId = viewModel.connectedEndpointId()
            Log.d("TransferHub", "ViewModel endpoint ID: $endpointId")
        }

        // Set up the view
        binding.btnSelectFiles.setOnClickListener {
            if (endpointId.isBlank()) {
                Log.e("TransferHub", "Cannot select files - no endpoint ID")
                com.google.android.material.snackbar.Snackbar
                    .make(binding.root, "Not connected to any device", com.google.android.material.snackbar.Snackbar.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            filePicker.launch(arrayOf("*/*"))
        }

        binding.btnDisconnect.setOnClickListener {
            viewModel.disconnect()
            findNavController().navigate(R.id.discoveryFragment)
        }
    }

    override fun observeData() {
        viewModel.filesStaged.collectLifecycleFlow(this) { staged ->
            if (staged) {
                Log.d("TransferHub", "Files staged, navigating to Queue")
                val bundle = android.os.Bundle().apply {
                    putString("endpointId", endpointId)
                }
                findNavController().navigate(R.id.action_transferHub_to_fileQueueReview, bundle)
                viewModel.resetStagedState()
            }
        }

        viewModel.connectedDeviceName.collectLifecycleFlow(this) { name ->
            Log.d("TransferHub", "Device name from ViewModel: '$name'")

            // Use the name from arguments if available, otherwise use from ViewModel
            val displayName = when {
                deviceName.isNotBlank() -> deviceName
                name.isNotBlank() -> name
                else -> "Device"
            }

            binding.tvConnectedDevice.text = getString(R.string.transfer_hub_connected_to, displayName)
        }
    }
}