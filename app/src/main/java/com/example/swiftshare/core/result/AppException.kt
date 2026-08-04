package com.example.swiftshare.core.result


sealed class AppException(override val message: String) : Exception(message) {

    data class NetworkError(
        override val message: String = "No internet connection. Please check your network."
    ) : AppException(message)

    data class ServerError(
        val code: Int,
        override val message: String
    ) : AppException(message)

    data class UnauthorizedError(
        override val message: String = "Session expired. Please log in again."
    ) : AppException(message)

    data class TimeoutError(
        override val message: String = "Request timed out. Please try again."
    ) : AppException(message)

    data class ParseError(
        override val message: String = "Something went wrong while reading the response."
    ) : AppException(message)

    data class UnknownError(
        override val message: String = "An unexpected error occurred."
    ) : AppException(message)
}
