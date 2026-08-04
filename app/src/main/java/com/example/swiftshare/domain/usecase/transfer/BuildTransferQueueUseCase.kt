package com.example.swiftshare.domain.usecase.transfer

import com.example.swiftshare.core.result.Result
import com.example.swiftshare.domain.repository.TransferRepository
import javax.inject.Inject

class BuildTransferQueueUseCase @Inject constructor(
    private val transferRepository: TransferRepository
) {
    suspend operator fun invoke(endpointId: String, fileUris: List<String>): Result<Unit> =
        transferRepository.queueFiles(endpointId, fileUris)
}