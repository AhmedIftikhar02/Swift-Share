package com.example.swiftshare.domain.model

/**
 * Stable, loggable error codes stamped onto a failed [FileTransferModel]. Kept as plain
 * string constants (rather than an enum) so they're safe to persist in Room and compare
 * across app versions without a migration. Referenced by both the Data layer (who sets
 * them) and, later, the History UI (Phase 10, who may show a friendlier label per code).
 */
object TransferErrorCode {
    const val SOURCE_FILE_MISSING = "source_file_missing"
    const val SOURCE_FILE_CHANGED = "source_file_changed"
    const val DEVICE_UNREACHABLE = "device_unreachable"
}