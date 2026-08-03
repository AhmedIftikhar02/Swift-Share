package com.example.swiftshare.domain.model

/** A nearby device, discovered or previously known. No Android/Nearby SDK types leak in here. */
data class DeviceModel(
    val endpointId: String,
    val displayName: String,
    val deviceType: DeviceType,
    val availability: DeviceAvailability,
    val isTrusted: Boolean = false,
    /** Non-empty only while the remote device is actively advertising a QR/PIN pairing
     *  code (Phase 5); empty during normal Discovery-tab browsing. */
    val pairingCode: String = ""
)

enum class DeviceType { PHONE, TABLET, UNKNOWN }

/** PRD 2.2 — live availability state shown next to each device in the Discovery list. */
enum class DeviceAvailability { AVAILABLE, BUSY, CONNECTING, UNAVAILABLE }