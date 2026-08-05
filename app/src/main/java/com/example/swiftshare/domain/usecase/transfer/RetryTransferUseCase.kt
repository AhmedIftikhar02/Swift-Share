package com.example.swiftshare.domain.usecase.transfer

import com.example.swiftshare.core.result.Result
import com.example.swiftshare.domain.repository.TransferRepository
import javax.inject.Inject

/**
 * Re-attempts a single previously failed or cancelled file transfer: re-validates the
 * source file still exists and hasn't changed, then re-establishes a connection to the
 * original recipient device if needed (PRD 2.13).
 */
class RetryTransferUseCase @Inject constructor(
    private val transferRepository: TransferRepository
) {
    suspend operator fun invoke(fileTransferId: String): Result<Unit> = transferRepository.retryFile(fileTransferId)
}