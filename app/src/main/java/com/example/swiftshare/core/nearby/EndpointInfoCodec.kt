package com.example.swiftshare.core.nearby

import android.util.Log

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