package com.example.swiftshare.presentation.completion.viewmodels

import androidx.lifecycle.SavedStateHandle
import com.example.swiftshare.base.BaseViewModel
import com.example.swiftshare.core.result.Result
import com.example.swiftshare.domain.model.TransferSessionModel
import com.example.swiftshare.domain.repository.HistoryRepository
import com.example.swiftshare.domain.usecase.transfer.ObserveActiveSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class CompletionUiState(
    val session: TransferSessionModel? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class CompletionViewModel @Inject constructor(
    private val observeActiveSessionUseCase: ObserveActiveSessionUseCase,
    private val historyRepository: HistoryRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    private val sessionId: String = savedStateHandle.get<String>("sessionId").orEmpty()

    private val _uiState = MutableStateFlow(CompletionUiState())
    val uiState: StateFlow<CompletionUiState> = _uiState.asStateFlow()

    init {
        launchSafe {
            if (sessionId.isBlank()) {
                _uiState.update { it.copy(isLoading = false) }
                return@launchSafe
            }
            // The session that just finished is almost always still cached in
            // TransferRepository's activeSession the instant this screen opens; fall back to
            // the persisted History record (already written by
            // TransferRepositoryImpl.finalizeSessionIfDone()) for the rarer case this screen is
            // reopened later, e.g. from a notification tap (wired for real in Phase 9).
            val cached = observeActiveSessionUseCase().first()
            val session = if (cached?.sessionId == sessionId) cached else null
                ?: (historyRepository.getSessionDetail(sessionId) as? Result.Success)?.data
            _uiState.update { it.copy(session = session, isLoading = false) }
        }
    }
}