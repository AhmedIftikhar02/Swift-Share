package com.example.swiftshare.core.result

sealed class Result<out T> {

    data class Success<out T>(val data: T) : Result<T>()

    data class Error(
        val exception: AppException
    ) : Result<Nothing>()

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

inline fun <T, R> Result<T>.map(transform: (T) -> R): Result<R> = when (this) {
    is Result.Success -> Result.Success(transform(data))
    is Result.Error -> this
}
