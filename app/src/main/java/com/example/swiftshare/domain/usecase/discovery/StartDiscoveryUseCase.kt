package com.example.swiftshare.domain.usecase.discovery

import com.example.swiftshare.core.result.Result
import com.example.swiftshare.domain.repository.NearbyRepository
import javax.inject.Inject

class StartDiscoveryUseCase @Inject constructor(
    private val nearbyRepository: NearbyRepository
) {
    suspend operator fun invoke(localDisplayName: String): Result<Unit> =
        nearbyRepository.startDiscovery(localDisplayName)
}