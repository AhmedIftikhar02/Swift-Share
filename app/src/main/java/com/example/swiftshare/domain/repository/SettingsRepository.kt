package com.example.swiftshare.domain.repository

import com.example.swiftshare.core.result.Result
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun observeDeviceDisplayName(): Flow<String>
    suspend fun setDeviceDisplayName(name: String): Result<Unit>
    fun observeAutoAcceptEnabled(): Flow<Boolean>
    suspend fun setAutoAcceptEnabled(enabled: Boolean): Result<Unit>
    fun observeDefaultSaveLocation(): Flow<String?>
    suspend fun setDefaultSaveLocation(uriPath: String): Result<Unit>
    suspend fun hasSeenOnboarding(): Boolean
    suspend fun setOnboardingSeen()
}