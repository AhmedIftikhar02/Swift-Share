package com.example.swiftshare.data.qr

import android.net.Uri


object QrPayloadCodec {
    private const val SCHEME = "swiftshare"
    private const val HOST = "pair"

    fun encode(pairingCode: String, displayName: String, expiresAtMillis: Long): String {
        val encodedName = Uri.encode(displayName)
        return Uri.Builder()
            .scheme(SCHEME)
            .authority(HOST)
            .appendQueryParameter("code", pairingCode)
            .appendQueryParameter("name", encodedName)
            .appendQueryParameter("exp", expiresAtMillis.toString())
            .build()
            .toString()
    }

    fun decode(raw: String): QrPayload? = runCatching {
        val uri = Uri.parse(raw)
        if (uri.scheme != SCHEME || uri.host != HOST) return null
        val code = uri.getQueryParameter("code") ?: return null
        val name = uri.getQueryParameter("name")?.let { Uri.decode(it) } ?: ""
        val expiresAt = uri.getQueryParameter("exp")?.toLongOrNull() ?: return null
        QrPayload(code = code, expiresAtMillis = expiresAt)
    }.getOrNull()
}
data class QrPayload(val code: String, val expiresAtMillis: Long) {
    val isExpired: Boolean get() = System.currentTimeMillis() > expiresAtMillis
}