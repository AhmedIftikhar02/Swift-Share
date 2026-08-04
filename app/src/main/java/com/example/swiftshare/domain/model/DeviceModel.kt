package com.example.swiftshare.domain.model

data class DeviceModel(
    val endpointId: String,
    val displayName: String,
    val deviceType: DeviceType,
    val availability: DeviceAvailability,
    val isTrusted: Boolean = false,
    val pairingCode: String = ""
)

enum class DeviceType { PHONE, TABLET, UNKNOWN }

enum class DeviceAvailability { AVAILABLE, BUSY, CONNECTING, UNAVAILABLE }