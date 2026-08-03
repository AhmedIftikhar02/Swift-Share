package com.example.swiftshare.domain.usecase.pairing

import com.example.swiftshare.core.result.Result
import com.example.swiftshare.domain.repository.NearbyRepository
import javax.inject.Inject

/** Confirms a pairing on this device's side after the user visually verifies the
 *  authentication token (PRD 2.3) — the connection only finalizes once BOTH sides call this. */
class AcceptConnectionUseCase @Inject constructor(
    private val nearbyRepository: NearbyRepository
) {
    suspend operator fun invoke(endpointId: String): Result<Unit> =
        nearbyRepository.acceptConnection(endpointId)
}