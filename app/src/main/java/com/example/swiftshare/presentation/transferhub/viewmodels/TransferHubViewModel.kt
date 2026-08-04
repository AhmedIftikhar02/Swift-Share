package com.example.swiftshare.presentation.transferhub.viewmodels

import com.example.swiftshare.base.BaseViewModel
import com.example.swiftshare.core.result.Result
import com.example.swiftshare.domain.model.ConnectionEvent
import com.example.swiftshare.domain.repository.NearbyRepository
import com.example.swiftshare.domain.usecase.transfer.BuildTransferQueueUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.util.Log
import androidx.lifecycle.viewModelScope

@HiltViewModel
class TransferHubViewModel @Inject constructor(
    private val nearbyRepository: NearbyRepository,
    private val buildTransferQueueUseCase: BuildTransferQueueUseCase
) : BaseViewModel() {

    private val _filesStaged = MutableStateFlow(false)
    val filesStaged: StateFlow<Boolean> = _filesStaged.asStateFlow()

    private val _connectedDeviceName = MutableStateFlow("")
    val connectedDeviceName: StateFlow<String> = _connectedDeviceName.asStateFlow()

    private var endpointId: String = ""

    init {
        viewModelScope.launch {
            nearbyRepository.observeConnectionEvents().collect { event ->
                when (event) {
                    is ConnectionEvent.ConnectionInitiated -> {
                        // If this is the connection we're establishing, store the name
                        if (event.endpointId == endpointId || endpointId.isEmpty()) {
                            endpointId = event.endpointId
                            val cleanName = event.remoteDeviceName
                                .replace("::PHONE::", "")
                                .replace("::TABLET::", "")
                                .replace("::UNKNOWN::", "")
                            _connectedDeviceName.value = cleanName
                            Log.d("TransferHubVM", "Connected to: $cleanName")
                        }
                    }
                    is ConnectionEvent.ConnectionResult -> {
                        if (event.success && event.endpointId == endpointId) {
                            Log.d("TransferHubVM", "Connection successful to: ${_connectedDeviceName.value}")
                        }
                    }
                    is ConnectionEvent.Disconnected -> {
                        if (event.endpointId == endpointId) {
                            Log.d("TransferHubVM", "Disconnected from: ${_connectedDeviceName.value}")
                            _connectedDeviceName.value = ""
                        }
                    }
                }
            }
        }
    }

    fun connectedEndpointId(): String = endpointId

    fun stageFiles(endpointId: String, uris: List<String>) = launchSafe {
        Log.d("TransferHubVM", "Staging ${uris.size} files for endpoint: $endpointId")
        this@TransferHubViewModel.endpointId = endpointId

        val result = buildTransferQueueUseCase(endpointId, uris)
        Log.d("TransferHubVM", "BuildTransferQueue result: $result")

        when (result) {
            is Result.Success -> {
                _filesStaged.value = true
                Log.d("TransferHubVM", "Files staged successfully")
            }
            is Result.Error -> {
                Log.e("TransferHubVM", "Error staging files: ${result.exception.message}")
                sendErrorEvent(result.exception.message ?: "Failed to stage files")
            }
        }
    }

    fun resetStagedState() {
        _filesStaged.value = false
    }

    fun disconnect() = launchSafe {
        if (endpointId.isNotBlank()) {
            nearbyRepository.disconnectFrom(endpointId)
        }
    }

    private fun sendErrorEvent(message: String) {
    }
}