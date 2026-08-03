package com.example.swiftshare.data.repository

import android.os.Build
import com.example.swiftshare.core.result.Result
import com.example.swiftshare.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor() : SettingsRepository {

    private val deviceDisplayName = MutableStateFlow(Build.MODEL ?: "My Device")
    private val autoAcceptEnabled = MutableStateFlow(false)
    private val defaultSaveLocation = MutableStateFlow<String?>(null)
    private var onboardingSeen = false

    override fun observeDeviceDisplayName(): Flow<String> = deviceDisplayName
    override suspend fun setDeviceDisplayName(name: String): Result<Unit> {
        deviceDisplayName.value = name
        return Result.Success(Unit)
    }

    override fun observeAutoAcceptEnabled(): Flow<Boolean> = autoAcceptEnabled
    override suspend fun setAutoAcceptEnabled(enabled: Boolean): Result<Unit> {
        autoAcceptEnabled.value = enabled
        return Result.Success(Unit)
    }

    override fun observeDefaultSaveLocation(): Flow<String?> = defaultSaveLocation
    override suspend fun setDefaultSaveLocation(uriPath: String): Result<Unit> {
        defaultSaveLocation.value = uriPath
        return Result.Success(Unit)
    }

    override suspend fun hasSeenOnboarding(): Boolean = onboardingSeen
    override suspend fun setOnboardingSeen() { onboardingSeen = true }
}