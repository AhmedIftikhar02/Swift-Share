package com.example.swiftshare.presentation.discovery.viewmodels

import androidx.lifecycle.viewModelScope
import com.example.swiftshare.base.BaseViewModel
import com.example.swiftshare.core.result.Result
import com.example.swiftshare.domain.model.DeviceModel
import com.example.swiftshare.domain.repository.SettingsRepository
import com.example.swiftshare.domain.usecase.discovery.ObserveNearbyDevicesUseCase
import com.example.swiftshare.domain.usecase.discovery.StartDiscoveryUseCase
import com.example.swiftshare.domain.usecase.discovery.StopDiscoveryUseCase
import com.example.swiftshare.domain.usecase.pairing.RequestConnectionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import android.util.Log

/**
 * Backs the Discovery (Home) screen (PRD 5.5). `launchSafe {}` (from `BaseViewModel`) ensures
 * any unexpected exception here surfaces as a `UiEvent.ShowError` instead of crashing the app.
 */
@HiltViewModel
class DiscoveryViewModel @Inject constructor(
    private val startDiscoveryUseCase: StartDiscoveryUseCase,
    private val stopDiscoveryUseCase: StopDiscoveryUseCase,
    private val observeNearbyDevicesUseCase: ObserveNearbyDevicesUseCase,
    private val requestConnectionUseCase: RequestConnectionUseCase,
    private val settingsRepository: SettingsRepository
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(DiscoveryUiState())
    val uiState: StateFlow<DiscoveryUiState> = _uiState.asStateFlow()

    private var deviceObservationStarted = false
    private var isScanning = false

    fun startScanning() = launchSafe {
        // Prevent multiple simultaneous scan starts
        if (isScanning) {
            Log.d("DiscoveryVM", "Already scanning, skipping")
            return@launchSafe
        }
        isScanning = true
        Log.d("DiscoveryVM", "Starting scan")

        _uiState.update { it.copy(isScanning = true, isInitialLoading = true, errorMessage = null) }

        val displayName = settingsRepository.observeDeviceDisplayName().first()
        when (val result = startDiscoveryUseCase(displayName)) {
            is Result.Success -> observeDevicesIfNeeded()
            is Result.Error -> {
                _uiState.update {
                    it.copy(isScanning = false, isInitialLoading = false, errorMessage = result.exception.message)
                }
                isScanning = false
                Log.e("DiscoveryVM", "Failed to start discovery: ${result.exception.message}")
            }
        }

        // PRD 2.1 Failure Case: no devices after 30s -> stop the loading spinner even with an
        // empty list, so the empty-state UI (not an endless shimmer) takes over.
        withTimeoutOrNull(30_000L) {
            observeNearbyDevicesUseCase().first { it.isNotEmpty() }
        }
        _uiState.update { it.copy(isInitialLoading = false) }
        isScanning = false
        Log.d("DiscoveryVM", "Scan completed")
    }

    fun stopScanning() = launchSafe {
        Log.d("DiscoveryVM", "Stopping scan")
        stopDiscoveryUseCase()
        isScanning = false
        _uiState.update { it.copy(isScanning = false) }
    }

    private fun observeDevicesIfNeeded() {
        if (deviceObservationStarted) return
        deviceObservationStarted = true
        viewModelScope.launch {
            observeNearbyDevicesUseCase().collect { devices ->
                _uiState.update { it.copy(devices = devices, isScanning = true, errorMessage = null) }
                Log.d("DiscoveryVM", "Devices updated: ${devices.size} devices")
            }
        }
    }

    fun rescan() {
        Log.d("DiscoveryVM", "Rescan requested")
        // Stop current scan first
        viewModelScope.launch {
            stopDiscoveryUseCase()
            isScanning = false
            _uiState.update { it.copy(isInitialLoading = true, errorMessage = null) }
            startScanning()
        }
    }

    fun onDeviceTapped(device: DeviceModel) {
        if (_uiState.value.connectingEndpointId != null) return // debounce double/rapid taps
        _uiState.update { it.copy(connectingEndpointId = device.endpointId) }
        launchSafe {
            when (val result = requestConnectionUseCase(device.endpointId)) {
                is Result.Success -> _uiState.update {
                    it.copy(connectingEndpointId = null, resolvedEndpointId = device.endpointId)
                }
                is Result.Error -> _uiState.update {
                    it.copy(connectingEndpointId = null, connectionErrorMessage = result.exception.message)
                }
            }
        }
    }

    fun consumeResolvedEndpoint() {
        _uiState.update { it.copy(resolvedEndpointId = null) }
    }

    fun consumeConnectionError() {
        _uiState.update { it.copy(connectionErrorMessage = null) }
    }

    override fun onCleared() {
        super.onCleared()
        launchSafe {
            stopDiscoveryUseCase()
            isScanning = false
        }
        Log.d("DiscoveryVM", "ViewModel cleared")
    }
}