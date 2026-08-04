package com.example.swiftshare.presentation.filequeue.viewmodels

import androidx.lifecycle.viewModelScope
import com.example.swiftshare.base.BaseViewModel
import com.example.swiftshare.base.UiEvent
import com.example.swiftshare.core.result.Result
import com.example.swiftshare.domain.model.QueuedFileModel
import com.example.swiftshare.domain.repository.TransferRepository
import com.example.swiftshare.domain.usecase.transfer.BuildTransferQueueUseCase
import com.example.swiftshare.domain.usecase.transfer.StartTransferUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.util.UUID
import javax.inject.Inject
import android.util.Log

data class FileQueueUiState(
    val files: List<QueuedFileModel> = emptyList(),
    val totalSizeBytes: Long = 0L,
    val isEmpty: Boolean = true,
    val isSending: Boolean = false
)

@HiltViewModel
class FileQueueReviewViewModel @Inject constructor(
    private val transferRepository: TransferRepository,
    private val buildTransferQueueUseCase: BuildTransferQueueUseCase,
    private val startTransferUseCase: StartTransferUseCase
) : BaseViewModel() {

    val uiState: StateFlow<FileQueueUiState> = transferRepository.observeQueue()
        .map { files ->
            FileQueueUiState(
                files = files,
                totalSizeBytes = files.sumOf { it.sizeBytes },
                isEmpty = files.isEmpty(),
                isSending = false  // Default to false
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = FileQueueUiState()
        )

    private val _navigateToSessionId = MutableStateFlow<String?>(null)
    val navigateToSessionId: StateFlow<String?> = _navigateToSessionId.asStateFlow()

    fun addFiles(endpointId: String, uris: List<String>) = launchSafe {
        Log.d("FileQueueVM", "Adding ${uris.size} files for endpoint: $endpointId")
        when (val result = buildTransferQueueUseCase(endpointId, uris)) {
            is Result.Success -> {
                Log.d("FileQueueVM", "Files added successfully")
            }
            is Result.Error -> {
                Log.e("FileQueueVM", "Error adding files: ${result.exception.message}")
                sendErrorEvent(result.exception.message ?: "Failed to add files")
            }
        }
    }

    fun removeFile(uri: String) = launchSafe {
        Log.d("FileQueueVM", "Removing file: $uri")
        transferRepository.removeFromQueue(uri)
    }

    fun sendFiles() = launchSafe {
        // Update loading state
        _uiState.update { it.copy(isSending = true) }

        val sessionId = UUID.randomUUID().toString()
        Log.d("FileQueueVM", "Sending files with sessionId: $sessionId")

        when (val result = startTransferUseCase(sessionId)) {
            is Result.Success -> {
                Log.d("FileQueueVM", "Send started successfully")
                _navigateToSessionId.value = sessionId
                _uiState.update { it.copy(isSending = false) }
            }
            is Result.Error -> {
                Log.e("FileQueueVM", "Send failed: ${result.exception.message}")
                _uiState.update { it.copy(isSending = false) }
                sendErrorEvent(result.exception.message ?: "Failed to send files")
            }
        }
    }

    fun consumeNavigation() {
        Log.d("FileQueueVM", "Consuming navigation")
        _navigateToSessionId.update { null }
    }

    private fun sendErrorEvent(message: String) {
        // Use BaseViewModel's emitEvent if available
        // emitEvent(UiEvent.ShowError(AppException.UnknownError(message)))
        // For now, log the error
        Log.e("FileQueueVM", "Error: $message")
    }

    private val _uiState = MutableStateFlow(FileQueueUiState())
}