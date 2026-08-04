package com.example.swiftshare.core.util

import java.io.File
import java.util.zip.CRC32
object ChecksumUtil {
    fun crc32Of(file: File): String = runCatching {
        val crc = CRC32()
        file.inputStream().use { input ->
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            var read: Int
            while (input.read(buffer).also { read = it } != -1) {
                crc.update(buffer, 0, read)
            }
        }
        crc.value.toString(16)
    }.getOrDefault("")
}