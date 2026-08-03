package com.example.swiftshare.data.qr

import android.net.Uri

/** Encodes/decodes the small payload embedded in the QR image — just enough to resolve the
 *  advertiser among currently-discovered endpoints (PRD 2.4); never the endpoint ID itself,
 *  since neither device knows a stable ID ahead of a live discovery session (PRD Section 10). */
object QrPayloadCodec {
    private const val SCHEME = "swiftshare"
    private const val HOST = "pair"

    fun encode(pairingCode: String, displayName: String, expiresAtMillis: Long): String {
        val encodedName = Uri.encode(displayName)  // Encode the display name
        return Uri.Builder()
            .scheme(SCHEME)
            .authority(HOST)
            .appendQueryParameter("code", pairingCode)
            .appendQueryParameter("name", encodedName)  // Use encoded name
            .appendQueryParameter("exp", expiresAtMillis.toString())
            .build()
            .toString()
    }

    fun decode(raw: String): QrPayload? = runCatching {
        val uri = Uri.parse(raw)
        if (uri.scheme != SCHEME || uri.host != HOST) return null
        val code = uri.getQueryParameter("code") ?: return null
        val name = uri.getQueryParameter("name")?.let { Uri.decode(it) } ?: ""  // Decode the name
        val expiresAt = uri.getQueryParameter("exp")?.toLongOrNull() ?: return null
        QrPayload(code = code, expiresAtMillis = expiresAt)
    }.getOrNull()
}
data class QrPayload(val code: String, val expiresAtMillis: Long) {
    val isExpired: Boolean get() = System.currentTimeMillis() > expiresAtMillis
}