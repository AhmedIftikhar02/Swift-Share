package com.example.swiftshare.core.result

/**
 * What a screen/section should render right now. Exposed from ViewModels as
 * StateFlow<UiState<T>>, collected by Fragments via repeatOnLifecycle (see
 * common/extensions/FlowExt.kt -> collectLifecycleFlow).
 *
 * Idle exists separately from Loading so a screen can distinguish "haven't fetched yet"
 * from "currently fetching" if that distinction matters for your UI (e.g. don't show a
 * shimmer until the user actually triggers a search).
 */
sealed class UiState<out T> {
    data object Idle : UiState<Nothing>()
    data object Loading : UiState<Nothing>()
    data class Success<out T>(val data: T) : UiState<T>()
    data class Error(val exception: AppException) : UiState<Nothing>()

    /** Distinct from Error: the call succeeded but returned nothing to show. */
    data object Empty : UiState<Nothing>()
}

/**
 * Converts a data Result into a UiState, treating an empty collection as UiState.Empty.
 * Optional helper - use where it fits, ignore where a plain map is clearer.
 */
inline fun <T> Result<T>.toUiState(isEmpty: (T) -> Boolean = { false }): UiState<T> = when (this) {
    is Result.Success -> if (isEmpty(data)) UiState.Empty else UiState.Success(data)
    is Result.Error -> UiState.Error(exception)
}
