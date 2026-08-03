package com.example.swiftshare.core.result

/**
 * Typed application-level exceptions. The safe API call wrapper (core/network/SafeApiCall.kt)
 * converts raw exceptions (IOException, HttpException, etc.) into one of these, so that
 * ViewModels and the UI layer never need to know about OkHttp/Retrofit-specific exception types -
 * they just branch on AppException subtype.
 */
sealed class AppException(override val message: String) : Exception(message) {

    /** No internet connection, DNS failure, socket timeout, etc. */
    data class NetworkError(
        override val message: String = "No internet connection. Please check your network."
    ) : AppException(message)

    /** Server responded with a non-2xx HTTP code. Carries the raw code for specific handling. */
    data class ServerError(
        val code: Int,
        override val message: String
    ) : AppException(message)

    /** 401 / 403 specifically - lets call sites trigger logout / token refresh flows. */
    data class UnauthorizedError(
        override val message: String = "Session expired. Please log in again."
    ) : AppException(message)

    /** Request timed out. */
    data class TimeoutError(
        override val message: String = "Request timed out. Please try again."
    ) : AppException(message)

    /** Response body didn't match the expected shape (Moshi parse failure, etc.). */
    data class ParseError(
        override val message: String = "Something went wrong while reading the response."
    ) : AppException(message)

    /** Catch-all for anything unexpected. Keep the original message for logging, but
     *  show the user something generic via UiText/string resources at the UI layer. */
    data class UnknownError(
        override val message: String = "An unexpected error occurred."
    ) : AppException(message)
}
