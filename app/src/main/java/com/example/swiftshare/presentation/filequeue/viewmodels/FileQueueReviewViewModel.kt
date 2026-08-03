package com.example.swiftshare.presentation.filequeue.viewmodels

import com.example.swiftshare.base.BaseViewModel
import com.example.swiftshare.core.result.Result
import com.example.swiftshare.domain.model.QueuedFileModel
import com.example.swiftshare.domain.repository.TransferRepository
import com.example.swiftshare.domain.usecase.transfer.BuildTransferQueueUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import android.util.Log
import androidx.lifecycle.viewModelScope

data class FileQueueUiState(
    val files: List<QueuedFileModel> = emptyList(),
    val totalSizeBytes: Long = 0L,
    val isEmpty: Boolean = true
)

@HiltViewModel
class FileQueueReviewViewModel @Inject constructor(
    private val transferRepository: TransferRepository,
    private val buildTransferQueueUseCase: BuildTransferQueueUseCase
) : BaseViewModel() {

    val uiState: StateFlow<FileQueueUiState> = transferRepository.observeQueue()
        .map { files ->
            FileQueueUiState(
                files = files,
                totalSizeBytes = files.sumOf { it.sizeBytes },
                isEmpty = files.isEmpty()
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = FileQueueUiState()
        )

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

    private fun sendErrorEvent(message: String) {
        // Emit error event if your BaseViewModel supports it
        // emitEvent(UiEvent.ShowError(AppException.UnknownError(message)))
    }
}