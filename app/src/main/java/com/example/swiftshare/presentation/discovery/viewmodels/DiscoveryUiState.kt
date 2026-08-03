package com.example.swiftshare.presentation.discovery.viewmodels

import com.example.swiftshare.domain.model.DeviceModel

/** Discovery screen's full render state (PRD 5.5). A dedicated class rather than raw
 *  `UiState<List<DeviceModel>>` because the screen needs to show devices AND a scanning
 *  indicator simultaneously (a non-empty list still "scanning" is a valid, common state). */
data class DiscoveryUiState(
    val devices: List<DeviceModel> = emptyList(),
    val isScanning: Boolean = false,
    val isInitialLoading: Boolean = true,
    val errorMessage: String? = null,
    /** BUGFIX (Phase 5): the endpoint a `requestConnection()` call is currently in flight for
     *  — lets the list disable/gray that row and show a spinner instead of allowing a second
     *  overlapping tap while the SDK call is pending. */
    val connectingEndpointId: String? = null,
    /** Non-null for exactly one state emission: the endpoint whose connection request just
     *  succeeded. The Fragment navigates to the Confirmation dialog once, then MUST call
     *  consumeResolvedEndpoint() — same one-shot-event pattern as Qr/PinPairingViewModel. */
    val resolvedEndpointId: String? = null,
    /** Transient error from a failed connection request, shown as a Snackbar and consumed —
     *  distinct from [errorMessage], which drives the full-screen error state for the device
     *  list itself and must stay sticky until a retry. */
    val connectionErrorMessage: String? = null
)