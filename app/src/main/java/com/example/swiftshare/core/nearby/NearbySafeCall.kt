package com.example.swiftshare.core.nearby

import com.example.swiftshare.core.result.AppException
import com.example.swiftshare.core.result.Result
import kotlinx.coroutines.CancellationException

/**
 * Wraps a Nearby Connections suspend call, converting any thrown exception into
 * [Result.Error]. [CancellationException] is re-thrown untouched so coroutine cancellation
 * (e.g. leaving a screen) is never swallowed as a fake "error" — mirrors the exact contract
 * the old `SafeApiCall` had for Retrofit calls.
 */
suspend fun <T> safeNearbyCall(block: suspend () -> T): Result<T> = try {
    Result.Success(block())
} catch (e: CancellationException) {
    throw e
} catch (e: Exception) {
    Result.Error(AppException.UnknownError(e.message ?: "Nearby Connections operation failed"))
}