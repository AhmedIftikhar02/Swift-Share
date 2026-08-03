package com.example.swiftshare.data.nearby

import android.util.Log
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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Thin, testable wrapper around Google's Nearby Connections `ConnectionsClient` (PRD Section
 * 10). Exposes discovered endpoints and connection lifecycle events as Kotlin Flows instead
 * of raw SDK callbacks — the ONLY class in the app that imports `com.google.android.gms.nearby.*`
 * directly, per the Domain-layer isolation rule (`PROJECT_CONTEXT.md` Section 5).
 */
@Singleton
class NearbyConnectionsDataSource @Inject constructor(
    private val connectionsClient: ConnectionsClient
) {
    private val serviceId = NearbyModule.SERVICE_ID

    private val _discoveredEndpoints = MutableStateFlow<Map<String, DecodedEndpointInfo>>(emptyMap())
    val discoveredEndpoints: StateFlow<Map<String, DecodedEndpointInfo>> = _discoveredEndpoints.asStateFlow()

    private val _connectionEvents = MutableSharedFlow<ConnectionEvent>(extraBufferCapacity = 16)
    val connectionEvents: SharedFlow<ConnectionEvent> = _connectionEvents.asSharedFlow()

    // BUGFIX (Phase 5): a plain SharedFlow with replay=0 silently drops an event emitted
    // before anyone is collecting. onConnectionInitiated can fire within a few ms of
    // requestConnection() returning — often before the Confirmation dialog's ViewModel has
    // finished being created and subscribed. This StateFlow-backed cache always holds the
    // latest pending confirmation per endpoint, so a late subscriber still sees it instead
    // of the dialog hanging forever with a blank auth token and a disabled Accept button.
    private val _pendingConfirmations =
        MutableStateFlow<Map<String, ConnectionEvent.ConnectionInitiated>>(emptyMap())
    val pendingConfirmations: StateFlow<Map<String, ConnectionEvent.ConnectionInitiated>> =
        _pendingConfirmations.asStateFlow()

    private var pendingIsIncoming: MutableMap<String, Boolean> = mutableMapOf()

    // Track advertising and discovery state to prevent duplicate starts
    private var isAdvertising = false
    private var isDiscovering = false

    private val endpointDiscoveryCallback = object : EndpointDiscoveryCallback() {
        override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
            if (info.serviceId != serviceId) return // PRD 10.1 de-dup: ignore other apps' ads
            val decoded = EndpointInfoCodec.decode(info.endpointName)
            _discoveredEndpoints.update { it + (endpointId to decoded) }
            Log.d("NearbyDataSource", "Endpoint found: $endpointId, name: ${decoded.displayName}")
        }

        override fun onEndpointLost(endpointId: String) {
            // PRD 2.1 Edge Case: device out of range before it was tapped — remove gracefully.
            _discoveredEndpoints.update { it - endpointId }
            Log.d("NearbyDataSource", "Endpoint lost: $endpointId")
        }
    }

    private val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, info: ConnectionInfo) {
            Log.d("NearbyDataSource", "onConnectionInitiated: endpoint=$endpointId, digits=${info.authenticationDigits}")
            pendingIsIncoming[endpointId] = info.isIncomingConnection
            val event = ConnectionEvent.ConnectionInitiated(
                endpointId = endpointId,
                remoteDeviceName = info.endpointName,
                authenticationDigits = info.authenticationDigits,
                isIncomingRequest = info.isIncomingConnection
            )
            _pendingConfirmations.update {
                Log.d("NearbyDataSource", "Updating pending confirmations: $endpointId")
                it + (endpointId to event)
            }
            _connectionEvents.tryEmit(event)
        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            Log.d("NearbyDataSource", "onConnectionResult: endpoint=$endpointId, success=${result.status.isSuccess}")
            pendingIsIncoming.remove(endpointId)
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
            Log.d("NearbyDataSource", "onDisconnected: endpoint=$endpointId")
            _pendingConfirmations.update { it - endpointId }
            _connectionEvents.tryEmit(ConnectionEvent.Disconnected(endpointId))
        }
    }

    /** No-op until Phase 7 wires real byte-level payload handling — accepting a connection
     *  still requires SOME PayloadCallback, so this satisfies the SDK contract safely now. */
    private val payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            // Wired to FileTransferDataSource in Phase 7.
        }
        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
            // Wired to ObserveTransferProgressUseCase in Phase 7.
        }
    }

    suspend fun startAdvertising(endpointName: String) {
        if (isAdvertising) {
            Log.d("NearbyDataSource", "Already advertising, skipping")
            return
        }

        try {
            Log.d("NearbyDataSource", "Starting advertising with name: $endpointName")
            val options = AdvertisingOptions.Builder().setStrategy(Strategy.P2P_CLUSTER).build()
            connectionsClient.startAdvertising(endpointName, serviceId, connectionLifecycleCallback, options).await()
            isAdvertising = true
            Log.d("NearbyDataSource", "Advertising started successfully")
        } catch (e: Exception) {
            Log.e("NearbyDataSource", "Failed to start advertising", e)
            throw e
        }
    }

    fun stopAdvertising() {
        if (!isAdvertising) {
            Log.d("NearbyDataSource", "Not advertising, skipping stop")
            return
        }
        try {
            connectionsClient.stopAdvertising()
            isAdvertising = false
            Log.d("NearbyDataSource", "Advertising stopped")
        } catch (e: Exception) {
            Log.e("NearbyDataSource", "Error stopping advertising", e)
            isAdvertising = false
        }
    }

    suspend fun startDiscovery() {
        if (isDiscovering) {
            Log.d("NearbyDataSource", "Already discovering, skipping")
            return
        }

        try {
            Log.d("NearbyDataSource", "Starting discovery")
            val options = DiscoveryOptions.Builder().setStrategy(Strategy.P2P_CLUSTER).build()
            connectionsClient.startDiscovery(serviceId, endpointDiscoveryCallback, options).await()
            isDiscovering = true
            Log.d("NearbyDataSource", "Discovery started successfully")
        } catch (e: Exception) {
            Log.e("NearbyDataSource", "Failed to start discovery", e)
            throw e
        }
    }

    fun stopDiscovery() {
        if (!isDiscovering) {
            Log.d("NearbyDataSource", "Not discovering, skipping stop")
            return
        }
        try {
            connectionsClient.stopDiscovery()
            _discoveredEndpoints.value = emptyMap()
            isDiscovering = false
            Log.d("NearbyDataSource", "Discovery stopped")
        } catch (e: Exception) {
            Log.e("NearbyDataSource", "Error stopping discovery", e)
            isDiscovering = false
        }
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
        isAdvertising = false
        isDiscovering = false
    }
}