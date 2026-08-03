package com.example.swiftshare.core.nearby

import android.util.Log

/**
 * Encodes/decodes the string every device advertises as its Nearby "endpoint name."
 * Format: "<displayName>::<deviceType>::<pairingCode>". `pairingCode` is empty during
 * normal Discovery-tab advertising and set only while a QR/PIN pairing session is active
 * (Phase 5). Kept intentionally simple (no JSON dependency) since Nearby endpoint names
 * are short, plain strings with a small size limit.
 */
object EndpointInfoCodec {
    private const val SEPARATOR = "::"

    fun encode(displayName: String, deviceType: String, pairingCode: String = ""): String {
        val safeName = displayName.take(40).replace(SEPARATOR, "-")
        val result = if (pairingCode.isEmpty()) {
            "${safeName}${SEPARATOR}${deviceType}"
        } else {
            "${safeName}${SEPARATOR}${deviceType}${SEPARATOR}${pairingCode}"
        }
        Log.d("EndpointInfoCodec", "encode: displayName=$displayName, deviceType=$deviceType, pairingCode=$pairingCode -> $result")
        return result
    }


    fun decode(raw: String): DecodedEndpointInfo {
        val parts = raw.split(SEPARATOR)
        Log.d("EndpointInfoCodec", "decode: raw=$raw, parts=$parts")
        return DecodedEndpointInfo(
            displayName = parts.getOrElse(0) { "Unknown Device" }.ifBlank { "Unknown Device" },
            deviceType = parts.getOrElse(1) { "UNKNOWN" },
            pairingCode = parts.getOrElse(2) { "" }
        )
    }
}

data class DecodedEndpointInfo(
    val displayName: String,
    val deviceType: String,
    val pairingCode: String
)