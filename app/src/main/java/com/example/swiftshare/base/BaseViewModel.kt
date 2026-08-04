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

abstract class BaseViewModel : ViewModel() {

    private val _uiEvent = MutableSharedFlow<UiEvent>(extraBufferCapacity = 1)
    val uiEvent: SharedFlow<UiEvent> = _uiEvent.asSharedFlow()

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Timber.tag("ViewModel").e(throwable, "Unhandled coroutine exception")
        emitEvent(UiEvent.ShowError(AppException.UnknownError(throwable.message ?: "Unknown error")))
    }
    protected fun launchSafe(block: suspend () -> Unit) {
        viewModelScope.launch(exceptionHandler) { block() }
    }

    protected fun emitEvent(event: UiEvent) {
        viewModelScope.launch { _uiEvent.emit(event) }
    }
}
sealed class UiEvent {
    data class ShowToast(val message: String) : UiEvent()
    data class ShowError(val exception: AppException) : UiEvent()
    data class Navigate(val route: String) : UiEvent()
    data object NavigateBack : UiEvent()
}
