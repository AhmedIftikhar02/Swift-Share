package com.example.swiftshare.domain.usecase.discovery

import com.example.swiftshare.domain.model.DeviceModel
import com.example.swiftshare.domain.repository.NearbyRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveNearbyDevicesUseCase @Inject constructor(
    private val nearbyRepository: NearbyRepository
) {
    operator fun invoke(): Flow<List<DeviceModel>> = nearbyRepository.observeNearbyDevices()
}