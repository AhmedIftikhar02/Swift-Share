package com.example.swiftshare.domain.repository

import com.example.swiftshare.core.result.Result
import com.example.swiftshare.domain.model.ConnectionEvent
import com.example.swiftshare.domain.model.ConnectionState
import com.example.swiftshare.domain.model.DeviceModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface NearbyRepository {

    val connectionState: StateFlow<ConnectionState>

    val connectedDevice: StateFlow<DeviceModel?>

    fun observeNearbyDevices(): Flow<List<DeviceModel>>
    fun observeConnectionEvents(): Flow<ConnectionEvent>
    fun observePendingConfirmation(endpointId: String): Flow<ConnectionEvent.ConnectionInitiated?>

    suspend fun startDiscovery(localDisplayName: String): Result<Unit>
    suspend fun stopDiscovery()

    suspend fun requestConnection(endpointId: String): Result<Unit>
    suspend fun acceptConnection(endpointId: String): Result<Unit>
    suspend fun rejectConnection(endpointId: String): Result<Unit>
    suspend fun disconnectFrom(endpointId: String)

    suspend fun startAdvertisingPairingCode(code: String): Result<Unit>
    suspend fun stopAdvertisingPairingCode(): Result<Unit>
    suspend fun findDeviceByPairingCode(code: String, timeoutMillis: Long = 15_000L): Result<DeviceModel>
}