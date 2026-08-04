package com.example.swiftshare.domain.usecase.transfer

import com.example.swiftshare.domain.model.TransferSessionModel
import com.example.swiftshare.domain.repository.TransferRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveActiveSessionUseCase @Inject constructor(
    private val transferRepository: TransferRepository
) {
    operator fun invoke(): Flow<TransferSessionModel?> = transferRepository.observeActiveSession()
}