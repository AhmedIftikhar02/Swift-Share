package com.example.swiftshare.domain.usecase.transfer

import com.example.swiftshare.core.result.Result
import com.example.swiftshare.domain.repository.TransferRepository
import javax.inject.Inject

/**
 * Resumes a previously paused transfer session. If the connection to the target device was
 * lost while paused, this attempts a single bounded reconnection window (PRD 10.6) before
 * resuming file delivery; files still mid-transfer when paused are re-sent from the start.
 */
class ResumeTransferUseCase @Inject constructor(
    private val transferRepository: TransferRepository
) {
    suspend operator fun invoke(sessionId: String): Result<Unit> = transferRepository.resumeTransfer(sessionId)
}