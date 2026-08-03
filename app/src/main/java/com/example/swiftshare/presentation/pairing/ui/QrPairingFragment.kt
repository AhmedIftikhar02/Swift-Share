package com.example.swiftshare.presentation.pairing.ui

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.swiftshare.R
import com.example.swiftshare.base.BaseFragment
import com.example.swiftshare.common.extensions.collectLifecycleFlow
import com.example.swiftshare.databinding.PairingFragmentQrBinding
import com.example.swiftshare.domain.repository.SettingsRepository
import com.example.swiftshare.presentation.pairing.viewmodels.QrPairingViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
@OptIn(ExperimentalGetImage::class)
class QrPairingFragment : BaseFragment<PairingFragmentQrBinding>(PairingFragmentQrBinding::inflate) {

    private val viewModel: QrPairingViewModel by viewModels()
    @Inject lateinit var settingsRepository: SettingsRepository

    private val barcodeScanner by lazy { BarcodeScanning.getClient() }
    private var cameraProvider: ProcessCameraProvider? = null
    private var imageAnalysis: ImageAnalysis? = null
    private var isScanning = false

    private val requestCameraPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            Log.d("QrPairing", "Camera permission granted")
            startCameraPreview()
        } else {
            Log.e("QrPairing", "Camera permission denied")
            showCameraDenied()
        }
    }

    override fun setupViews() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) = showTab(tab.position)
            override fun onTabUnselected(tab: TabLayout.Tab) = Unit
            override fun onTabReselected(tab: TabLayout.Tab) = Unit
        })
        showTab(0)

        binding.btnRegenerate.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                val displayName = settingsRepository.observeDeviceDisplayName().first()
                viewModel.regenerate(displayName)
            }
        }

        // Generate initial QR code
        viewLifecycleOwner.lifecycleScope.launch {
            val displayName = settingsRepository.observeDeviceDisplayName().first()
            viewModel.generateCode(displayName)
        }
    }

    override fun observeData() {
        viewModel.uiState.collectLifecycleFlow(this) { state ->
            Log.d("QrPairing", "UI State: isResolving=${state.isResolving}, isExpired=${state.isExpired}")

            // Show QR code
            state.qrBitmap?.let {
                binding.ivQrCode.setImageBitmap(it)
            }

            binding.tvCountdown.text = getString(R.string.pairing_seconds_remaining, state.secondsRemaining)
            binding.groupExpired.visibility = if (state.isExpired) android.view.View.VISIBLE else android.view.View.GONE
            binding.ivQrCode.alpha = if (state.isExpired) 0.3f else 1.0f

            binding.progressResolving.visibility =
                if (state.isResolving) android.view.View.VISIBLE else android.view.View.GONE

            // Show scan result messages
            state.scanResultMessage?.let { message ->
                Log.d("QrPairing", "Scan result: $message")
                Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
                viewModel.consumeScanResultMessage()
            }

            // Navigate to confirmation when a device is resolved
            state.resolvedEndpointId?.let { endpointId ->
                Log.d("QrPairing", "Resolved endpoint: $endpointId, navigating to confirmation")
                val bundle = android.os.Bundle().apply { putString("endpointId", endpointId) }
                findNavController().navigate(R.id.action_qrPairing_to_connectionConfirmation, bundle)
                viewModel.consumeResolvedEndpoint()
            }
        }
    }

    private fun showTab(position: Int) {
        binding.layoutMyCode.visibility = if (position == 0) android.view.View.VISIBLE else android.view.View.GONE
        binding.layoutScan.visibility = if (position == 1) android.view.View.VISIBLE else android.view.View.GONE

        if (position == 1) {
            Log.d("QrPairing", "Switched to Scan tab")
            checkCameraPermissionAndStart()
        } else {
            Log.d("QrPairing", "Switched to My Code tab")
            stopCameraPreview()
        }
    }

    private fun checkCameraPermissionAndStart() {
        val granted = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED

        Log.d("QrPairing", "Camera permission granted: $granted")

        if (granted) {
            startCameraPreview()
        } else {
            requestCameraPermission.launch(Manifest.permission.CAMERA)
        }
    }

    private fun startCameraPreview() {
        Log.d("QrPairing", "Starting camera preview")

        binding.tvCameraDenied.visibility = android.view.View.GONE
        binding.previewView.visibility = android.view.View.VISIBLE

        val providerFuture = ProcessCameraProvider.getInstance(requireContext())
        providerFuture.addListener({
            try {
                val provider = providerFuture.get()
                cameraProvider = provider
                provider.unbindAll()

                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(binding.previewView.surfaceProvider)
                }

                imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()

                imageAnalysis?.setAnalyzer(ContextCompat.getMainExecutor(requireContext())) { imageProxy ->
                    if (isScanning) {
                        imageProxy.close()
                        return@setAnalyzer
                    }
                    isScanning = true

                    try {
                        val mediaImage = imageProxy.image
                        if (mediaImage != null) {
                            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

                            barcodeScanner.process(image)
                                .addOnSuccessListener { barcodes ->
                                    barcodes.forEach { barcode ->
                                        Log.d("QrPairing", "Barcode found: type=${barcode.valueType}, raw=${barcode.rawValue}")
                                    }

                                    val barcode = barcodes.firstOrNull {
                                        it.valueType == Barcode.TYPE_URL || it.rawValue != null
                                    }

                                    barcode?.rawValue?.let { rawValue ->
                                        Log.d("QrPairing", "Processing QR code: $rawValue")
                                        viewModel.onQrScanned(rawValue)
                                    }
                                }
                                .addOnFailureListener { e ->
                                    Log.e("QrPairing", "Barcode scan failed", e)
                                }
                                .addOnCompleteListener {
                                    isScanning = false
                                    imageProxy.close()
                                }
                        } else {
                            isScanning = false
                            imageProxy.close()
                        }
                    } catch (e: Exception) {
                        Log.e("QrPairing", "Error processing image", e)
                        isScanning = false
                        imageProxy.close()
                    }
                }

                val camera = provider.bindToLifecycle(
                    viewLifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageAnalysis
                )

                Log.d("QrPairing", "Camera started successfully")

            } catch (e: Exception) {
                Log.e("QrPairing", "Failed to start camera", e)
                Snackbar.make(binding.root, R.string.pairing_camera_error, Snackbar.LENGTH_LONG).show()
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun stopCameraPreview() {
        Log.d("QrPairing", "Stopping camera preview")
        try {
            cameraProvider?.unbindAll()
            cameraProvider = null
            imageAnalysis = null
            isScanning = false
        } catch (e: Exception) {
            Log.e("QrPairing", "Error stopping camera", e)
        }
    }

    private fun showCameraDenied() {
        Log.e("QrPairing", "Showing camera denied message")
        binding.tvCameraDenied.visibility = android.view.View.VISIBLE
        binding.previewView.visibility = android.view.View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopCameraPreview()
    }

    override fun onPause() {
        super.onPause()
        // Stop scanning when fragment is paused to save resources
        stopCameraPreview()
    }

    override fun onResume() {
        super.onResume()
        // Restart camera if we're on the scan tab
        if (binding.tabLayout.selectedTabPosition == 1) {
            checkCameraPermissionAndStart()
        }
    }
}