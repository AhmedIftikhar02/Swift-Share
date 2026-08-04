package com.example.swiftshare.data.repository

import android.content.Context
import android.content.res.Configuration
import android.util.Log
import com.example.swiftshare.core.nearby.EndpointInfoCodec
import com.example.swiftshare.core.nearby.safeNearbyCall
import com.example.swiftshare.core.result.AppException
import com.example.swiftshare.core.result.Result
import com.example.swiftshare.common.providers.DispatcherProvider
import com.example.swiftshare.data.nearby.NearbyConnectionsDataSource
import com.example.swiftshare.domain.model.ConnectionEvent
import com.example.swiftshare.domain.model.ConnectionState
import com.example.swiftshare.domain.model.DeviceAvailability
import com.example.swiftshare.domain.model.DeviceModel
import com.example.swiftshare.domain.model.DeviceType
import com.example.swiftshare.domain.repository.NearbyRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class NearbyRepositoryImpl @Inject constructor(
    private val dataSource: NearbyConnectionsDataSource,
    private val dispatcherProvider: DispatcherProvider,
    @ApplicationContext private val context: Context
) : NearbyRepository {


    private val repositoryScope = CoroutineScope(SupervisorJob() + dispatcherProvider.default)

    private val _connectionState = MutableStateFlow(ConnectionState.IDLE)
    override val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private val _connectedDevice = MutableStateFlow<DeviceModel?>(null)
    override val connectedDevice: StateFlow<DeviceModel?> = _connectedDevice.asStateFlow()

    private val remoteNamesByEndpoint = mutableMapOf<String, String>()

    private var localDisplayName: String = android.os.Build.MODEL ?: "My Device"
    private var isDiscovering = false
    private var currentEndpointId: String? = null

    init {
        repositoryScope.launch {
            dataSource.connectionEvents.collect { event ->
                Log.d("NearbyRepo", "Connection event: $event")

                when (event) {
                    is ConnectionEvent.ConnectionInitiated -> {
                        remoteNamesByEndpoint[event.endpointId] = event.remoteDeviceName
                        _connectionState.value = ConnectionState.CONNECTING
                        Log.d("NearbyRepo", "Connection initiated, storing name: ${event.remoteDeviceName}")
                    }
                    is ConnectionEvent.ConnectionResult -> {
                        if (event.success) {
                            val deviceName = remoteNamesByEndpoint[event.endpointId] ?: "Unknown Device"
                            _connectedDevice.value = DeviceModel(
                                endpointId = event.endpointId,
                                displayName = deviceName,
                                deviceType = DeviceType.UNKNOWN,
                                availability = DeviceAvailability.AVAILABLE
                            )
                            currentEndpointId = event.endpointId
                            _connectionState.value = ConnectionState.CONNECTED
                            Log.d("NearbyRepo", "Connection successful, connected to: $deviceName")
                        } else {
                            _connectedDevice.value = null
                            currentEndpointId = null
                            _connectionState.value = ConnectionState.IDLE
                            Log.d("NearbyRepo", "Connection failed")
                        }
                    }
                    is ConnectionEvent.Disconnected -> {
                        _connectedDevice.value = null
                        currentEndpointId = null
                        _connectionState.value = ConnectionState.IDLE
                        Log.d("NearbyRepo", "Disconnected")
                    }
                }
            }
        }
    }

    override fun observeNearbyDevices(): Flow<List<DeviceModel>> =
        dataSource.discoveredEndpoints.map { endpoints ->
            endpoints.map { (endpointId, info) ->
                DeviceModel(
                    endpointId = endpointId,
                    displayName = info.displayName,
                    deviceType = runCatching { DeviceType.valueOf(info.deviceType) }
                        .getOrDefault(DeviceType.UNKNOWN),
                    availability = DeviceAvailability.AVAILABLE,
                    pairingCode = info.pairingCode
                )
            }
        }

    override fun observeConnectionEvents(): Flow<ConnectionEvent> = dataSource.connectionEvents

    override fun observePendingConfirmation(endpointId: String): Flow<ConnectionEvent.ConnectionInitiated?> =
        dataSource.pendingConfirmations.map {
            val event = it[endpointId]
            Log.d("NearbyRepo", "Pending confirmation for $endpointId: $event")
            event
        }

    override suspend fun startDiscovery(localDisplayName: String): Result<Unit> {
        if (isDiscovering) {
            Log.d("NearbyRepo", "Already discovering, skipping")
            return Result.Success(Unit)
        }

        this.localDisplayName = localDisplayName
        _connectionState.value = ConnectionState.DISCOVERING

        val advertiseResult = safeNearbyCall {
            dataSource.startAdvertising(EndpointInfoCodec.encode(localDisplayName, currentDeviceType().name))
        }
        if (advertiseResult is Result.Error) {
            _connectionState.value = ConnectionState.IDLE
            return advertiseResult
        }

        val discoveryResult = safeNearbyCall { dataSource.startDiscovery() }
        if (discoveryResult is Result.Error) {
            dataSource.stopAdvertising()
            _connectionState.value = ConnectionState.IDLE
            return discoveryResult
        }

        isDiscovering = true
        Log.d("NearbyRepo", "Discovery started successfully")
        return Result.Success(Unit)
    }

    override suspend fun stopDiscovery() {
        dataSource.stopDiscovery()
        dataSource.stopAdvertising()
        isDiscovering = false
        if (_connectionState.value == ConnectionState.DISCOVERING) {
            _connectionState.value = ConnectionState.IDLE
        }
        Log.d("NearbyRepo", "Discovery stopped")
    }

    override suspend fun requestConnection(endpointId: String): Result<Unit> {
        _connectionState.value = ConnectionState.CONNECTING
        currentEndpointId = endpointId
        Log.d("NearbyRepo", "Requesting connection to: $endpointId")
        val result = safeNearbyCall { dataSource.requestConnection(localDisplayName, endpointId) }
        if (result is Result.Error) {
            _connectionState.value = ConnectionState.DISCOVERING
            currentEndpointId = null
            Log.e("NearbyRepo", "Connection request failed: ${result.exception.message}")
        }
        return result
    }

    override suspend fun acceptConnection(endpointId: String): Result<Unit> =
        safeNearbyCall { dataSource.acceptConnection(endpointId) }

    override suspend fun rejectConnection(endpointId: String): Result<Unit> {
        val result = safeNearbyCall { dataSource.rejectConnection(endpointId) }
        _connectionState.value = ConnectionState.DISCOVERING
        currentEndpointId = null
        _connectedDevice.value = null
        remoteNamesByEndpoint.remove(endpointId)
        Log.d("NearbyRepo", "Rejected connection to: $endpointId")
        return result
    }

    override suspend fun disconnectFrom(endpointId: String) {
        _connectionState.value = ConnectionState.DISCONNECTING
        Log.d("NearbyRepo", "Disconnecting from: $endpointId")
        dataSource.disconnectFromEndpoint(endpointId)
        _connectionState.value = ConnectionState.DISCOVERING
        currentEndpointId = null
        _connectedDevice.value = null
        remoteNamesByEndpoint.remove(endpointId)
        Log.d("NearbyRepo", "Disconnected")
    }

    override suspend fun startAdvertisingPairingCode(code: String): Result<Unit> {
        Log.d("NearbyRepo", "startAdvertisingPairingCode: $code")
        dataSource.stopAdvertising()
        val endpointName = EndpointInfoCodec.encode(
            localDisplayName,
            currentDeviceType().name,
            code
        )
        Log.d("NearbyRepo", "Advertising with endpoint name: $endpointName")
        return safeNearbyCall {
            dataSource.startAdvertising(endpointName)
        }
    }

    override suspend fun stopAdvertisingPairingCode(): Result<Unit> {
        Log.d("NearbyRepo", "stopAdvertisingPairingCode")
        dataSource.stopAdvertising()
        return safeNearbyCall {
            dataSource.startAdvertising(
                EndpointInfoCodec.encode(localDisplayName, currentDeviceType().name)
            )
        }
    }

    override suspend fun findDeviceByPairingCode(code: String, timeoutMillis: Long): Result<DeviceModel> {
        Log.d("NearbyRepo", "Looking for device with code: $code, timeout: ${timeoutMillis}ms")

        val startTime = System.currentTimeMillis()
        var found: DeviceModel? = null

        while (found == null && System.currentTimeMillis() - startTime < timeoutMillis) {
            val devices = observeNearbyDevices().first()
            Log.d("NearbyRepo", "Checking ${devices.size} devices for code: $code")
            devices.forEach { device ->
                Log.d("NearbyRepo", "Device: ${device.displayName}, code: ${device.pairingCode}")
            }
            found = devices.firstOrNull { it.pairingCode == code }
            if (found == null) {
                kotlinx.coroutines.delay(500L)
            }
        }

        return found?.let {
            Log.d("NearbyRepo", "Found device: ${it.displayName} with code: $code")
            Result.Success(it)
        } ?: run {
            Log.e("NearbyRepo", "No device found with code: $code after ${System.currentTimeMillis() - startTime}ms")
            Result.Error(AppException.UnknownError("No device found for that code. Make sure both devices are nearby and the code hasn't expired."))
        }
    }

    private fun currentDeviceType(): DeviceType {
        val isTablet = (context.resources.configuration.screenLayout and
                Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE
        return if (isTablet) DeviceType.TABLET else DeviceType.PHONE
    }
}