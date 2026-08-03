package com.example.swiftshare.domain.usecase.transfer

import com.example.swiftshare.core.result.Result
import com.example.swiftshare.domain.repository.TransferRepository
import javax.inject.Inject

/** Hands a confirmed batch of SAF URIs to the Transfer Repository to be queued (PRD 2.6/2.7). */
class BuildTransferQueueUseCase @Inject constructor(
    private val transferRepository: TransferRepository
) {
    suspend operator fun invoke(endpointId: String, fileUris: List<String>): Result<Unit> =
        transferRepository.queueFiles(endpointId, fileUris)
}