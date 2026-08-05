package com.example.swiftshare.domain.usecase.transfer

import com.example.swiftshare.core.result.Result
import com.example.swiftshare.domain.repository.TransferRepository
import javax.inject.Inject

/**
 * Pauses the given in-progress transfer session, preserving each file's transferred-byte
 * count so the UI can keep showing progress while paused. Only applies to sessions this
 * device is sending (direction = SENT) — see [TransferRepository.pauseTransfer].
 */
class PauseTransferUseCase @Inject constructor(
    private val transferRepository: TransferRepository
) {
    suspend operator fun invoke(sessionId: String): Result<Unit> = transferRepository.pauseTransfer(sessionId)
}