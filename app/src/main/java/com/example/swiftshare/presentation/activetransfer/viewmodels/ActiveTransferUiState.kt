package com.example.swiftshare.presentation.activetransfer.viewmodels

import com.example.swiftshare.domain.model.TransferSessionModel

data class ActiveTransferUiState(
    val session: TransferSessionModel? = null,
    val overallProgressPercent: Int = 0,
    val transferredBytes: Long = 0L,
    val totalBytes: Long = 0L,
    val speedBytesPerSecond: Long = 0L,
    val etaSeconds: Long? = null,
    val isComplete: Boolean = false,
    val isPaused: Boolean = false,
    val isReconnecting: Boolean = false,
    val canPauseResume: Boolean = false
    // Phase 9: navigateToCompletion removed — MainActivity now owns all completion
    // navigation globally (see section 6), so a single Fragment-local one-shot flag is
    // no longer needed and would risk double-navigating.
)