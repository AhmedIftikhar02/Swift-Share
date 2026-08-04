package com.example.swiftshare.presentation.pairing.viewmodels

import androidx.lifecycle.viewModelScope
import com.example.swiftshare.base.BaseViewModel
import com.example.swiftshare.core.result.Result
import com.example.swiftshare.domain.model.ConnectionEvent
import com.example.swiftshare.domain.repository.NearbyRepository
import com.example.swiftshare.domain.usecase.pairing.AcceptConnectionUseCase
import com.example.swiftshare.domain.usecase.pairing.RejectConnectionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import android.os.CountDownTimer
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ConfirmationUiState(
    val remoteDeviceName: String = "",
    val authenticationDigits: String = "",
    val secondsRemaining: Int = 30,
    val isResolved: Boolean = false,
    val isAccepted: Boolean = false,
    val errorMessage: String? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class ConnectionConfirmationViewModel @Inject constructor(
    private val nearbyRepository: NearbyRepository,
    private val acceptConnectionUseCase: AcceptConnectionUseCase,
    private val rejectConnectionUseCase: RejectConnectionUseCase
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(ConfirmationUiState())
    val uiState: StateFlow<ConfirmationUiState> = _uiState.asStateFlow()

    private var endpointId: String? = null
    private var countDownTimer: CountDownTimer? = null
    private var isObserving = false

    fun observe(endpointId: String) {
        if (isObserving) {
            Log.d("ConfirmationVM", "Already observing, skipping")
            return
        }
        isObserving = true

        this.endpointId = endpointId
        Log.d("ConfirmationVM", "Observing endpoint: $endpointId")

        _uiState.value = ConfirmationUiState(isLoading = true)

        viewModelScope.launch {
            try {
                val pending = nearbyRepository.observePendingConfirmation(endpointId).first()
                Log.d("ConfirmationVM", "Cached pending confirmation: $pending")

                if (pending != null) {
                    _uiState.value = _uiState.value.copy(
                        remoteDeviceName = pending.remoteDeviceName,
                        authenticationDigits = pending.authenticationDigits,
                        isLoading = false
                    )
                    startCountdown()
                    return@launch
                }
            } catch (e: Exception) {
                Log.e("ConfirmationVM", "Error reading pending confirmation", e)
            }

            Log.d("ConfirmationVM", "No cached pending confirmation found, waiting for live event")
        }

        viewModelScope.launch {
            nearbyRepository.observeConnectionEvents()
                .filter { it is ConnectionEvent.ConnectionInitiated && it.endpointId == endpointId }
                .collect { event ->
                    Log.d("ConfirmationVM", "Received live event: $event")
                    if (event is ConnectionEvent.ConnectionInitiated) {
                        _uiState.value = _uiState.value.copy(
                            remoteDeviceName = event.remoteDeviceName,
                            authenticationDigits = event.authenticationDigits,
                            isLoading = false
                        )
                        if (countDownTimer == null) startCountdown()
                    }
                }
        }

        viewModelScope.launch {
            nearbyRepository.observeConnectionEvents().collect { event ->
                when {
                    event is ConnectionEvent.ConnectionResult && event.endpointId == endpointId -> {
                        Log.d("ConfirmationVM", "Connection result: ${event.success}")
                        countDownTimer?.cancel()
                        _uiState.value = _uiState.value.copy(
                            isResolved = true,
                            isAccepted = event.success,
                            errorMessage = if (!event.success) event.statusMessage else null,
                            isLoading = false
                        )
                    }
                    event is ConnectionEvent.Disconnected && event.endpointId == endpointId -> {
                        Log.d("ConfirmationVM", "Disconnected")
                        countDownTimer?.cancel()
                        _uiState.value = _uiState.value.copy(
                            isResolved = true,
                            isAccepted = false,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    fun accept() = launchSafe {
        val id = endpointId ?: return@launchSafe
        Log.d("ConfirmationVM", "Accepting connection for: $id")
        when (val result = acceptConnectionUseCase(id)) {
            is Result.Error -> _uiState.value = _uiState.value.copy(errorMessage = result.exception.message)
            else -> Unit
        }
    }

    fun reject() = launchSafe {
        val id = endpointId ?: return@launchSafe
        Log.d("ConfirmationVM", "Rejecting connection for: $id")
        rejectConnectionUseCase(id)
        countDownTimer?.cancel()
        _uiState.value = _uiState.value.copy(isResolved = true, isAccepted = false)
    }

    private fun startCountdown() {
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(30_000L, 1_000L) {
            override fun onTick(millisUntilFinished: Long) {
                _uiState.value = _uiState.value.copy(secondsRemaining = (millisUntilFinished / 1000L).toInt())
            }
            override fun onFinish() {
                Log.d("ConfirmationVM", "Countdown finished, rejecting")
                reject()
            }
        }.start()
    }

    override fun onCleared() {
        super.onCleared()
        countDownTimer?.cancel()
    }
}