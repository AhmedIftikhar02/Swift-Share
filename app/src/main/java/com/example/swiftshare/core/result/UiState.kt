package com.example.swiftshare.core.result

sealed class UiState<out T> {
    data object Idle : UiState<Nothing>()
    data object Loading : UiState<Nothing>()
    data class Success<out T>(val data: T) : UiState<T>()
    data class Error(val exception: AppException) : UiState<Nothing>()
    data object Empty : UiState<Nothing>()
}

inline fun <T> Result<T>.toUiState(isEmpty: (T) -> Boolean = { false }): UiState<T> = when (this) {
    is Result.Success -> if (isEmpty(data)) UiState.Empty else UiState.Success(data)
    is Result.Error -> UiState.Error(exception)
}
