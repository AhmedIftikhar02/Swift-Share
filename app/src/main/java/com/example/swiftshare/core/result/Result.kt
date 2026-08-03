package com.example.swiftshare.core.result

/**
 * Generic wrapper for any operation that can succeed or fail.
 * Used as the return type of Repository functions, Retrofit calls (via safeApiCall), etc.
 *
 * Keeping this separate from UiState (core/result/UiState.kt) is intentional:
 * - Result = "did the data layer call succeed" (domain/data layer concern)
 * - UiState = "what should the screen show right now" (presentation layer concern,
 *   includes Loading which Result intentionally does NOT have)
 *
 * ViewModels typically convert a Result into a UiState.
 */
sealed class Result<out T> {

    data class Success<out T>(val data: T) : Result<T>()

    data class Error(
        val exception: AppException
    ) : Result<Nothing>()

    /**
     * Returns the data if Success, or null otherwise. Useful for quick checks
     * without a full when() block.
     */
    fun getOrNull(): T? = when (this) {
        is Success -> data
        is Error -> null
    }

    inline fun onSuccess(action: (T) -> Unit): Result<T> {
        if (this is Success) action(data)
        return this
    }

    inline fun onError(action: (AppException) -> Unit): Result<T> {
        if (this is Error) action(exception)
        return this
    }
}

/**
 * Maps Success data from one type to another while leaving Error untouched.
 * Common use: mapping a network DTO Result<UserDto> into a domain Result<User>.
 */
inline fun <T, R> Result<T>.map(transform: (T) -> R): Result<R> = when (this) {
    is Result.Success -> Result.Success(transform(data))
    is Result.Error -> this
}
