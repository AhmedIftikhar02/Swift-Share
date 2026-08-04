package com.example.swiftshare.core.nearby

import com.example.swiftshare.core.result.AppException
import com.example.swiftshare.core.result.Result
import kotlinx.coroutines.CancellationException

suspend fun <T> safeNearbyCall(block: suspend () -> T): Result<T> = try {
    Result.Success(block())
} catch (e: CancellationException) {
    throw e
} catch (e: Exception) {
    Result.Error(AppException.UnknownError(e.message ?: "Nearby Connections operation failed"))
}