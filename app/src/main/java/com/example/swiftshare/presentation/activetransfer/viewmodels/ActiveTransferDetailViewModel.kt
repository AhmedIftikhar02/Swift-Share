package com.example.swiftshare.presentation.activetransfer.viewmodels

import androidx.lifecycle.viewModelScope
import com.example.swiftshare.base.BaseViewModel
import com.example.swiftshare.domain.model.SessionStatus
import com.example.swiftshare.domain.usecase.transfer.CancelTransferUseCase
import com.example.swiftshare.domain.usecase.transfer.ObserveActiveSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private val TERMINAL_STATUSES = setOf(
    SessionStatus.COMPLETED, SessionStatus.FAILED, SessionStatus.PARTIAL, SessionStatus.CANCELLED
)

@HiltViewModel
class ActiveTransferDetailViewModel @Inject constructor(
    private val observeActiveSessionUseCase: ObserveActiveSessionUseCase,
    private val cancelTransferUseCase: CancelTransferUseCase
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(ActiveTransferUiState())
    val uiState: StateFlow<ActiveTransferUiState> = _uiState.asStateFlow()

    private val speedTracker = SpeedTracker()

    init {
        viewModelScope.launch {
            observeActiveSessionUseCase().collect { session ->
                if (session == null) return@collect

                val total = session.files.sumOf { it.totalBytes }
                val transferred = session.files.sumOf { it.transferredBytes }
                val percent = if (total > 0) ((transferred * 100) / total).toInt().coerceIn(0, 100) else 0
                val speed = speedTracker.sample(transferred)
                val remaining = (total - transferred).coerceAtLeast(0)
                val eta = if (speed > 0) remaining / speed else null
                val terminal = session.status in TERMINAL_STATUSES

                _uiState.update { current ->
                    current.copy(
                        session = session,
                        overallProgressPercent = percent,
                        transferredBytes = transferred,
                        totalBytes = total,
                        speedBytesPerSecond = speed,
                        etaSeconds = eta,
                        isComplete = terminal,
                        navigateToCompletion = terminal && !current.isComplete
                    )
                }
            }
        }
    }

    fun cancelTransfer() = launchSafe {
        _uiState.value.session?.let { cancelTransferUseCase(it.sessionId) }
    }

    fun consumeNavigateToCompletion() {
        _uiState.update { it.copy(navigateToCompletion = false) }
    }
}

private class SpeedTracker {
    private var lastBytes = 0L
    private var lastTimestamp = 0L
    private var lastSpeed = 0L

    fun sample(currentBytes: Long): Long {
        val now = System.currentTimeMillis()
        if (lastTimestamp == 0L) {
            lastTimestamp = now
            lastBytes = currentBytes
            return 0L
        }
        val elapsedMs = now - lastTimestamp
        if (elapsedMs < 500L) return lastSpeed
        val deltaBytes = (currentBytes - lastBytes).coerceAtLeast(0L)
        lastSpeed = if (elapsedMs > 0) (deltaBytes * 1000L) / elapsedMs else 0L
        lastBytes = currentBytes
        lastTimestamp = now
        return lastSpeed
    }
}