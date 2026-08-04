package com.example.swiftshare.domain.usecase.pairing

import com.example.swiftshare.core.result.Result
import com.example.swiftshare.domain.repository.NearbyRepository
import javax.inject.Inject

class RequestConnectionUseCase @Inject constructor(
    private val nearbyRepository: NearbyRepository
) {
    suspend operator fun invoke(endpointId: String): Result<Unit> =
        nearbyRepository.requestConnection(endpointId)
}