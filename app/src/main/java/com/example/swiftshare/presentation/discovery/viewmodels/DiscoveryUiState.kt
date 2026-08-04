package com.example.swiftshare.presentation.discovery.viewmodels

import com.example.swiftshare.domain.model.DeviceModel


data class DiscoveryUiState(
    val devices: List<DeviceModel> = emptyList(),
    val isScanning: Boolean = false,
    val isInitialLoading: Boolean = true,
    val errorMessage: String? = null,
    val connectingEndpointId: String? = null,
    val resolvedEndpointId: String? = null,
    val connectionErrorMessage: String? = null
)