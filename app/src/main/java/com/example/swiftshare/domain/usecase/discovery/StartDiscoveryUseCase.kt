package com.example.swiftshare.domain.usecase.discovery

import com.example.swiftshare.core.result.Result
import com.example.swiftshare.domain.repository.NearbyRepository
import javax.inject.Inject

/** Starts dual-role advertising + discovery (PRD 10.1) under the user's current display name. */
class StartDiscoveryUseCase @Inject constructor(
    private val nearbyRepository: NearbyRepository
) {
    suspend operator fun invoke(localDisplayName: String): Result<Unit> =
        nearbyRepository.startDiscovery(localDisplayName)
}