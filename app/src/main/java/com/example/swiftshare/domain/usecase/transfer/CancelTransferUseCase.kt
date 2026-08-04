package com.example.swiftshare.domain.usecase.transfer

import com.example.swiftshare.core.result.Result
import com.example.swiftshare.domain.repository.TransferRepository
import javax.inject.Inject

class CancelTransferUseCase @Inject constructor(
    private val transferRepository: TransferRepository
) {
    suspend operator fun invoke(sessionId: String): Result<Unit> = transferRepository.cancelTransfer(sessionId)
}