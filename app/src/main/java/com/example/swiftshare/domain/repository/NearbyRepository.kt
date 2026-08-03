package com.example.swiftshare.domain.repository

import com.example.swiftshare.core.result.Result
import com.example.swiftshare.domain.model.ConnectionEvent
import com.example.swiftshare.domain.model.ConnectionState
import com.example.swiftshare.domain.model.DeviceModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * Wraps the Nearby Connections SDK (advertising, discovery, connection lifecycle).
 * Real implementation: `NearbyRepositoryImpl` (Section 5.2), backed by
 * `NearbyConnectionsDataSource` (PRD Section 10).
 */
interface NearbyRepository {

    /** Single source of truth for the app's current connection phase (PRD 7.6). */
    val connectionState: StateFlow<ConnectionState>

    fun observeNearbyDevices(): Flow<List<DeviceModel>>
    fun observeConnectionEvents(): Flow<ConnectionEvent>

    /** BUGFIX (Phase 5): the current pending confirmation for [endpointId], if any — a
     *  StateFlow-backed read that always has the latest value, so a screen that starts
     *  observing after the SDK's `onConnectionInitiated` already fired still gets it
     *  immediately instead of waiting on a live event that already happened. */
    fun observePendingConfirmation(endpointId: String): Flow<ConnectionEvent.ConnectionInitiated?>

    suspend fun startDiscovery(localDisplayName: String): Result<Unit>
    suspend fun stopDiscovery()

    suspend fun requestConnection(endpointId: String): Result<Unit>
    suspend fun acceptConnection(endpointId: String): Result<Unit>
    suspend fun rejectConnection(endpointId: String): Result<Unit>
    suspend fun disconnectFrom(endpointId: String)

    /** Restarts advertising with a pairing code embedded (Phase 5 QR/PIN "My Code"/"My PIN"
     *  tabs), without interrupting the app's ongoing dual-role discovery (PRD 10.1). */
    suspend fun startAdvertisingPairingCode(code: String): Result<Unit>

    /** Reverts to plain (code-less) advertising once a QR/PIN session ends or expires. */
    suspend fun stopAdvertisingPairingCode(): Result<Unit>

    /** Resolves a scanned/typed pairing code to a currently-discovered device, waiting up to
     *  [timeoutMillis] in case the advertiser hasn't been discovered yet at the exact moment
     *  of lookup. Returns an error if no match appears in time (PRD 2.4/2.5 Failure Cases). */
    suspend fun findDeviceByPairingCode(code: String, timeoutMillis: Long = 5_000L): Result<DeviceModel>
}