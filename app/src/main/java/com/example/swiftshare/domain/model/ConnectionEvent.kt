package com.example.swiftshare.domain.model

sealed class ConnectionEvent {
    data class ConnectionInitiated(
        val endpointId: String,
        val remoteDeviceName: String,
        val authenticationDigits: String,
        val isIncomingRequest: Boolean
    ) : ConnectionEvent()

    data class ConnectionResult(
        val endpointId: String,
        val success: Boolean,
        val statusMessage: String
    ) : ConnectionEvent()

    data class Disconnected(val endpointId: String) : ConnectionEvent()
}