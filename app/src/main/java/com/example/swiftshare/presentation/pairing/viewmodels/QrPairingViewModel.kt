package com.example.swiftshare.presentation.pairing.viewmodels

import android.graphics.Bitmap
import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.swiftshare.base.BaseViewModel
import com.example.swiftshare.core.result.Result
import com.example.swiftshare.data.qr.QrCodeGenerator
import com.example.swiftshare.data.qr.QrPayloadCodec
import com.example.swiftshare.domain.repository.NearbyRepository
import com.example.swiftshare.domain.usecase.pairing.RequestConnectionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.security.SecureRandom
import javax.inject.Inject

private const val QR_VALIDITY_MS = 120_000L

data class QrPairingUiState(
    val qrBitmap: Bitmap? = null,
    val secondsRemaining: Int = 0,
    val isExpired: Boolean = false,
    val scanResultMessage: String? = null,
    val isResolving: Boolean = false,
    val resolvedEndpointId: String? = null
)

@HiltViewModel
class QrPairingViewModel @Inject constructor(
    private val nearbyRepository: NearbyRepository,
    private val requestConnectionUseCase: RequestConnectionUseCase,
    private val qrCodeGenerator: QrCodeGenerator
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(QrPairingUiState())
    val uiState: StateFlow<QrPairingUiState> = _uiState.asStateFlow()

    private var countDownTimer: CountDownTimer? = null

    fun generateCode(displayName: String) = launchSafe {
        Log.d("QrPairingVM", "Generating QR code for: $displayName")
        val code = generateRandomCode()
        val expiresAt = System.currentTimeMillis() + QR_VALIDITY_MS
        val payload = QrPayloadCodec.encode(code, displayName, expiresAt)
        val bitmap = qrCodeGenerator.generate(payload)

        Log.d("QrPairingVM", "QR code generated with code: $code")
        _uiState.value = _uiState.value.copy(qrBitmap = bitmap, isExpired = false)

        val result = nearbyRepository.startAdvertisingPairingCode(code)
        if (result is Result.Error) {
            Log.e("QrPairingVM", "Failed to start advertising: ${result.exception.message}")
        } else {
            Log.d("QrPairingVM", "Advertising started with pairing code")
        }
        startCountdown()
    }

    fun regenerate(displayName: String) = generateCode(displayName)

    fun onQrScanned(rawValue: String) {
        Log.d("QrPairingVM", "QR scanned: $rawValue")

        if (_uiState.value.isResolving) {
            Log.d("QrPairingVM", "Already resolving, ignoring scan")
            return
        }

        val payload = QrPayloadCodec.decode(rawValue)
        if (payload == null) {
            Log.e("QrPairingVM", "Invalid QR payload")
            _uiState.value = _uiState.value.copy(scanResultMessage = "That doesn't look like a SwiftShare code.")
            return
        }

        if (payload.isExpired) {
            Log.e("QrPairingVM", "QR code expired")
            _uiState.value = _uiState.value.copy(scanResultMessage = "This code has expired. Ask for a new one.")
            return
        }

        Log.d("QrPairingVM", "Valid QR code: ${payload.code}")
        _uiState.value = _uiState.value.copy(isResolving = true, scanResultMessage = null)

        viewModelScope.launch {
            try {
                Log.d("QrPairingVM", "Looking for device with code: ${payload.code}")

                val deviceResult = nearbyRepository.findDeviceByPairingCode(payload.code, 10_000L)

                when (deviceResult) {
                    is Result.Success -> {
                        Log.d("QrPairingVM", "Found device: ${deviceResult.data.displayName}")
                        val connectResult = requestConnectionUseCase(deviceResult.data.endpointId)
                        when (connectResult) {
                            is Result.Success -> {
                                Log.d("QrPairingVM", "Connection request sent")
                                _uiState.value = _uiState.value.copy(
                                    isResolving = false,
                                    resolvedEndpointId = deviceResult.data.endpointId
                                )
                            }
                            is Result.Error -> {
                                Log.e("QrPairingVM", "Connection request failed: ${connectResult.exception.message}")
                                _uiState.value = _uiState.value.copy(
                                    isResolving = false,
                                    scanResultMessage = connectResult.exception.message
                                )
                            }
                        }
                    }
                    is Result.Error -> {
                        Log.e("QrPairingVM", "Device not found: ${deviceResult.exception.message}")
                        _uiState.value = _uiState.value.copy(
                            isResolving = false,
                            scanResultMessage = "No device found with that code. Make sure both devices are nearby and the code hasn't expired."
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("QrPairingVM", "Error during QR resolution", e)
                _uiState.value = _uiState.value.copy(
                    isResolving = false,
                    scanResultMessage = "An error occurred. Please try again."
                )
            }
        }
    }

    fun consumeScanResultMessage() {
        _uiState.value = _uiState.value.copy(scanResultMessage = null)
    }

    fun consumeResolvedEndpoint() {
        _uiState.value = _uiState.value.copy(resolvedEndpointId = null)
    }

    private fun startCountdown() {
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(QR_VALIDITY_MS, 1_000L) {
            override fun onTick(millisUntilFinished: Long) {
                _uiState.value = _uiState.value.copy(secondsRemaining = (millisUntilFinished / 1000L).toInt())
            }
            override fun onFinish() {
                Log.d("QrPairingVM", "QR code expired")
                _uiState.value = _uiState.value.copy(isExpired = true)
                launchSafe {
                    nearbyRepository.stopAdvertisingPairingCode()
                }
            }
        }.start()
    }

    private fun generateRandomCode(): String {
        val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        val random = SecureRandom()
        return (1..8).map { chars[random.nextInt(chars.length)] }.joinToString("")
    }

    override fun onCleared() {
        super.onCleared()
        countDownTimer?.cancel()
        launchSafe {
            nearbyRepository.stopAdvertisingPairingCode()
        }
        Log.d("QrPairingVM", "ViewModel cleared")
    }
}