package com.example.swiftshare.data.nearby

import android.net.Uri
import com.example.swiftshare.core.nearby.DecodedEndpointInfo
import com.example.swiftshare.core.nearby.EndpointInfoCodec
import com.example.swiftshare.di.NearbyModule
import com.google.android.gms.nearby.connection.AdvertisingOptions
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback
import com.google.android.gms.nearby.connection.ConnectionResolution
import com.google.android.gms.nearby.connection.ConnectionsClient
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo
import com.google.android.gms.nearby.connection.DiscoveryOptions
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.PayloadCallback
import com.google.android.gms.nearby.connection.PayloadTransferUpdate
import com.google.android.gms.nearby.connection.Strategy
import com.example.swiftshare.domain.model.ConnectionEvent
import com.example.swiftshare.domain.model.PayloadEvent
import com.example.swiftshare.domain.model.PayloadStatus
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NearbyConnectionsDataSource @Inject constructor(
    private val connectionsClient: ConnectionsClient
) {
    private val serviceId = NearbyModule.SERVICE_ID

    private val _discoveredEndpoints = MutableStateFlow<Map<String, DecodedEndpointInfo>>(emptyMap())
    val discoveredEndpoints: StateFlow<Map<String, DecodedEndpointInfo>> = _discoveredEndpoints.asStateFlow()

    private val _connectionEvents = MutableSharedFlow<ConnectionEvent>(extraBufferCapacity = 16)
    val connectionEvents: SharedFlow<ConnectionEvent> = _connectionEvents.asSharedFlow()

    private val _pendingConfirmations =
        MutableStateFlow<Map<String, ConnectionEvent.ConnectionInitiated>>(emptyMap())
    val pendingConfirmations: StateFlow<Map<String, ConnectionEvent.ConnectionInitiated>> =
        _pendingConfirmations.asStateFlow()


    private val _payloadEvents = MutableSharedFlow<PayloadEvent>(extraBufferCapacity = 64)
    val payloadEvents: SharedFlow<PayloadEvent> = _payloadEvents.asSharedFlow()


    private val incomingFilePayloads = mutableMapOf<Long, Payload>()

    private val endpointDiscoveryCallback = object : EndpointDiscoveryCallback() {
        override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
            if (info.serviceId != serviceId) return
            val decoded = EndpointInfoCodec.decode(info.endpointName)
            _discoveredEndpoints.update { it + (endpointId to decoded) }
        }

        override fun onEndpointLost(endpointId: String) {
            _discoveredEndpoints.update { it - endpointId }
        }
    }

    private val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, info: ConnectionInfo) {
            val event = ConnectionEvent.ConnectionInitiated(
                endpointId = endpointId,
                remoteDeviceName = info.endpointName,
                authenticationDigits = info.authenticationDigits,
                isIncomingRequest = info.isIncomingConnection
            )
            _pendingConfirmations.update { it + (endpointId to event) }
            _connectionEvents.tryEmit(event)
        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            _pendingConfirmations.update { it - endpointId }
            _connectionEvents.tryEmit(
                ConnectionEvent.ConnectionResult(
                    endpointId = endpointId,
                    success = result.status.isSuccess,
                    statusMessage = result.status.statusMessage ?: "Status ${result.status.statusCode}"
                )
            )
        }

        override fun onDisconnected(endpointId: String) {
            _pendingConfirmations.update { it - endpointId }
            _connectionEvents.tryEmit(ConnectionEvent.Disconnected(endpointId))
        }
    }


    private val payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            when (payload.type) {
                Payload.Type.BYTES -> {
                    val bytes = payload.asBytes()
                    if (bytes != null) {
                        _payloadEvents.tryEmit(PayloadEvent.BytesReceived(endpointId, payload.id, bytes))
                    }
                }
                Payload.Type.FILE -> {
                    incomingFilePayloads[payload.id] = payload
                    _payloadEvents.tryEmit(PayloadEvent.FileIncomingStarted(endpointId, payload.id))
                }
                else -> Unit
            }
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
            val status = when (update.status) {
                PayloadTransferUpdate.Status.SUCCESS -> PayloadStatus.SUCCESS
                PayloadTransferUpdate.Status.FAILURE -> PayloadStatus.FAILURE
                PayloadTransferUpdate.Status.CANCELED -> PayloadStatus.CANCELED
                else -> PayloadStatus.IN_PROGRESS
            }
            _payloadEvents.tryEmit(
                PayloadEvent.TransferUpdate(update.payloadId, update.bytesTransferred, update.totalBytes, status)
            )
        }
    }

    suspend fun startAdvertising(endpointName: String) {
        val options = AdvertisingOptions.Builder().setStrategy(Strategy.P2P_CLUSTER).build()
        connectionsClient.startAdvertising(endpointName, serviceId, connectionLifecycleCallback, options).await()
    }

    fun stopAdvertising() {
        connectionsClient.stopAdvertising()
    }

    suspend fun startDiscovery() {
        val options = DiscoveryOptions.Builder().setStrategy(Strategy.P2P_CLUSTER).build()
        connectionsClient.startDiscovery(serviceId, endpointDiscoveryCallback, options).await()
    }

    fun stopDiscovery() {
        connectionsClient.stopDiscovery()
        _discoveredEndpoints.value = emptyMap()
    }

    suspend fun requestConnection(localEndpointName: String, endpointId: String) {
        connectionsClient.requestConnection(localEndpointName, endpointId, connectionLifecycleCallback).await()
    }

    suspend fun acceptConnection(endpointId: String) {
        connectionsClient.acceptConnection(endpointId, payloadCallback).await()
    }

    suspend fun rejectConnection(endpointId: String) {
        connectionsClient.rejectConnection(endpointId).await()
    }

    fun disconnectFromEndpoint(endpointId: String) {
        connectionsClient.disconnectFromEndpoint(endpointId)
    }

    fun stopAllEndpoints() {
        connectionsClient.stopAllEndpoints()
        _discoveredEndpoints.value = emptyMap()
    }

    suspend fun sendBytesPayload(endpointId: String, bytes: ByteArray): Long {
        val payload = Payload.fromBytes(bytes)
        connectionsClient.sendPayload(endpointId, payload).await()
        return payload.id
    }

    suspend fun sendFilePayload(endpointId: String, pfd: android.os.ParcelFileDescriptor): Long {
        val payload = Payload.fromFile(pfd)
        connectionsClient.sendPayload(endpointId, payload).await()
        return payload.id
    }


    fun cancelPayload(payloadId: Long) {
        runCatching { connectionsClient.cancelPayload(payloadId) }
    }

    fun takeReceivedFileUri(payloadId: Long): Uri? {
        val payload = incomingFilePayloads.remove(payloadId) ?: run {
            Timber.tag("NearbyDataSource").w("takeReceivedFileUri: no cached payload for id=%d", payloadId)
            return null
        }

        val uri = runCatching { payload.asFile()?.asUri() }
            .onFailure { Timber.tag("NearbyDataSource").e(it, "asUri() threw for payload=%d", payloadId) }
            .getOrNull()

        if (uri == null) {
            Timber.tag("NearbyDataSource").e("asUri() returned null for payload=%d", payloadId)
        }
        return uri
    }
}