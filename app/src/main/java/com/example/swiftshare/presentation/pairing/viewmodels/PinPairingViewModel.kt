package com.example.swiftshare.presentation.pairing.viewmodels

import androidx.lifecycle.viewModelScope
import com.example.swiftshare.base.BaseViewModel
import com.example.swiftshare.core.result.Result
import com.example.swiftshare.domain.repository.NearbyRepository
import com.example.swiftshare.domain.usecase.pairing.RequestConnectionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import android.os.CountDownTimer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.security.SecureRandom
import javax.inject.Inject

private const val PIN_VALIDITY_MS = 120_000L

data class PinPairingUiState(
    val myPin: String = "",
    val secondsRemaining: Int = 0,
    val isExpired: Boolean = false,
    val enteredPin: String = "",
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val resolvedEndpointId: String? = null
)

@HiltViewModel
class PinPairingViewModel @Inject constructor(
    private val nearbyRepository: NearbyRepository,
    private val requestConnectionUseCase: RequestConnectionUseCase
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(PinPairingUiState())
    val uiState: StateFlow<PinPairingUiState> = _uiState.asStateFlow()

    private var countDownTimer: CountDownTimer? = null

    fun generatePin() = launchSafe {
        val pin = (100000 + SecureRandom().nextInt(900000)).toString() // 6 digits, PRD 2.5
        _uiState.value = _uiState.value.copy(myPin = pin, isExpired = false)
        nearbyRepository.startAdvertisingPairingCode(pin)
        startCountdown()
    }

    fun regeneratePin() = generatePin()

    fun onPinInputChanged(value: String) {
        _uiState.value = _uiState.value.copy(enteredPin = value.filter { it.isDigit() }.take(6), errorMessage = null)
    }

    fun consumeResolvedEndpoint() {
        _uiState.value = _uiState.value.copy(resolvedEndpointId = null)
    }

    fun submitPin() {
        val pin = _uiState.value.enteredPin
        if (pin.length != 6) {
            _uiState.value = _uiState.value.copy(errorMessage = "Enter the full 6-digit PIN.")
            return
        }
        _uiState.value = _uiState.value.copy(isSubmitting = true, errorMessage = null)
        viewModelScope.launch {
            when (val deviceResult = nearbyRepository.findDeviceByPairingCode(pin)) {
                is Result.Success -> {
                    when (val connectResult = requestConnectionUseCase(deviceResult.data.endpointId)) {
                        is Result.Success -> _uiState.value = _uiState.value.copy(
                            isSubmitting = false, resolvedEndpointId = deviceResult.data.endpointId
                        )
                        is Result.Error -> _uiState.value = _uiState.value.copy(
                            isSubmitting = false, errorMessage = connectResult.exception.message
                        )
                    }
                }
                is Result.Error -> _uiState.value = _uiState.value.copy(
                    isSubmitting = false,
                    errorMessage = "That PIN doesn't match any nearby device. It may be wrong or expired."
                )
            }
        }
    }

    private fun startCountdown() {
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(PIN_VALIDITY_MS, 1_000L) {
            override fun onTick(millisUntilFinished: Long) {
                _uiState.value = _uiState.value.copy(secondsRemaining = (millisUntilFinished / 1000L).toInt())
            }
            override fun onFinish() {
                _uiState.value = _uiState.value.copy(isExpired = true)
                launchSafe { nearbyRepository.stopAdvertisingPairingCode() }
            }
        }.start()
    }

    override fun onCleared() {
        super.onCleared()
        countDownTimer?.cancel()
        launchSafe { nearbyRepository.stopAdvertisingPairingCode() }
    }
}