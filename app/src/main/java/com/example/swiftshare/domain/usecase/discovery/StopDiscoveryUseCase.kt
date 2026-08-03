package com.example.swiftshare.domain.usecase.discovery

import com.example.swiftshare.domain.repository.NearbyRepository
import javax.inject.Inject

class StopDiscoveryUseCase @Inject constructor(
    private val nearbyRepository: NearbyRepository
) {
    suspend operator fun invoke() = nearbyRepository.stopDiscovery()
}