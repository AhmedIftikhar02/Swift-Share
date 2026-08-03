package com.example.swiftshare.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.swiftshare.core.result.AppException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Common ViewModel base. Most of your screen-specific state should still live in each
 * ViewModel's own StateFlow<UiState<T>> (see home/presentation/viewmodels/HomeViewModel.kt
 * for the canonical example) - this base class only holds things genuinely shared by
 * every screen: a one-shot event channel for things like toasts/snackbars/navigation
 * that shouldn't replay on configuration change (StateFlow would replay them; this won't),
 * and a safe launch helper that won't crash the app on an uncaught coroutine exception.
 */
abstract class BaseViewModel : ViewModel() {

    private val _uiEvent = MutableSharedFlow<UiEvent>(extraBufferCapacity = 1)
    val uiEvent: SharedFlow<UiEvent> = _uiEvent.asSharedFlow()

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Timber.tag("ViewModel").e(throwable, "Unhandled coroutine exception")
        emitEvent(UiEvent.ShowError(AppException.UnknownError(throwable.message ?: "Unknown error")))
    }

    /** Launch a coroutine in viewModelScope with a safety net - an unexpected exception
     *  shows an error event instead of crashing the app. Use this instead of raw
     *  viewModelScope.launch for anything that calls into the data layer. */
    protected fun launchSafe(block: suspend () -> Unit) {
        viewModelScope.launch(exceptionHandler) { block() }
    }

    protected fun emitEvent(event: UiEvent) {
        viewModelScope.launch { _uiEvent.emit(event) }
    }
}

/** One-shot events - things that should happen exactly once, not be re-shown on rotation. */
sealed class UiEvent {

    data class ShowToast(val message: String) : UiEvent()

    data class ShowError(val exception: AppException) : UiEvent()

    data class Navigate(val route: String) : UiEvent()

    data object NavigateBack : UiEvent()
}
