package com.example.swiftshare.presentation.activetransfer.viewmodels

import androidx.lifecycle.viewModelScope
import com.example.swiftshare.base.BaseViewModel
import com.example.swiftshare.base.UiEvent
import com.example.swiftshare.core.result.Result
import com.example.swiftshare.domain.model.SessionStatus
import com.example.swiftshare.domain.model.TransferDirection
import com.example.swiftshare.domain.usecase.transfer.CancelTransferUseCase
import com.example.swiftshare.domain.usecase.transfer.ObserveActiveSessionUseCase
import com.example.swiftshare.domain.usecase.transfer.PauseTransferUseCase
import com.example.swiftshare.domain.usecase.transfer.ResumeTransferUseCase
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
    private val cancelTransferUseCase: CancelTransferUseCase,
    private val pauseTransferUseCase: PauseTransferUseCase,
    private val resumeTransferUseCase: ResumeTransferUseCase
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
                val terminal = session.status in TERMINAL_STATUSES
                val paused = session.status == SessionStatus.PAUSED
                val reconnecting = session.status == SessionStatus.RECONNECTING
                val speed = if (paused || reconnecting) _uiState.value.speedBytesPerSecond else speedTracker.sample(transferred)
                val remaining = (total - transferred).coerceAtLeast(0)
                val eta = if (speed > 0 && !paused && !reconnecting) remaining / speed else null

                _uiState.update { current ->
                    current.copy(
                        session = session,
                        overallProgressPercent = percent,
                        transferredBytes = transferred,
                        totalBytes = total,
                        speedBytesPerSecond = speed,
                        etaSeconds = eta,
                        isComplete = terminal,
                        isPaused = paused,
                        isReconnecting = reconnecting,
                        canPauseResume = session.direction == TransferDirection.SENT && !terminal
                    )
                }
            }
        }
    }

    fun cancelTransfer() = launchSafe {
        _uiState.value.session?.let { cancelTransferUseCase(it.sessionId) }
    }

    fun togglePauseResume() = launchSafe {
        val session = _uiState.value.session ?: return@launchSafe
        val result = when (session.status) {
            SessionStatus.IN_PROGRESS -> pauseTransferUseCase(session.sessionId)
            SessionStatus.PAUSED, SessionStatus.RECONNECTING -> resumeTransferUseCase(session.sessionId)
            else -> return@launchSafe
        }
        if (result is Result.Error) {
            emitEvent(UiEvent.ShowError(result.exception))
        }
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