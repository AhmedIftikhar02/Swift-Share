package com.example.swiftshare.domain.model

/**
 * One-shot Nearby connection lifecycle events (PRD 10.3/10.4), distinct from
 * [ConnectionState] — this carries the *reason* a state transition happened (e.g. the
 * authentication token to display, or which endpoint disconnected), while ConnectionState
 * is just the current phase.
 */
sealed class ConnectionEvent {
    /** Mirrors Nearby's onConnectionInitiated — fired on BOTH ends of a pairing attempt. */
    data class ConnectionInitiated(
        val endpointId: String,
        val remoteDeviceName: String,
        val authenticationDigits: String,
        val isIncomingRequest: Boolean
    ) : ConnectionEvent()

    /** Mirrors onConnectionResult. */
    data class ConnectionResult(
        val endpointId: String,
        val success: Boolean,
        val statusMessage: String
    ) : ConnectionEvent()

    /** Mirrors onDisconnected. */
    data class Disconnected(val endpointId: String) : ConnectionEvent()
}