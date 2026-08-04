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
    val navigateToCompletion: Boolean = false
)