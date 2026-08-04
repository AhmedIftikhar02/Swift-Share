package com.example.swiftshare.data.repository

import android.os.Build
import com.example.swiftshare.core.result.Result
import com.example.swiftshare.data.local.AppPreferences
import com.example.swiftshare.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val appPreferences: AppPreferences
) : SettingsRepository {

    private val deviceDisplayName = MutableStateFlow(
        appPreferences.deviceDisplayName ?: (Build.MODEL ?: "My Device")
    )
    private val autoAcceptEnabled = MutableStateFlow(appPreferences.autoAcceptEnabled)
    private val defaultSaveLocation = MutableStateFlow(appPreferences.defaultSaveLocation)

    override fun observeDeviceDisplayName(): Flow<String> = deviceDisplayName
    override suspend fun setDeviceDisplayName(name: String): Result<Unit> {
        appPreferences.deviceDisplayName = name
        deviceDisplayName.value = name
        return Result.Success(Unit)
    }

    override fun observeAutoAcceptEnabled(): Flow<Boolean> = autoAcceptEnabled
    override suspend fun setAutoAcceptEnabled(enabled: Boolean): Result<Unit> {
        appPreferences.autoAcceptEnabled = enabled
        autoAcceptEnabled.value = enabled
        return Result.Success(Unit)
    }

    override fun observeDefaultSaveLocation(): Flow<String?> = defaultSaveLocation
    override suspend fun setDefaultSaveLocation(uriPath: String): Result<Unit> {
        appPreferences.defaultSaveLocation = uriPath
        defaultSaveLocation.value = uriPath
        return Result.Success(Unit)
    }

    override suspend fun hasSeenOnboarding(): Boolean = appPreferences.hasSeenOnboarding
    override suspend fun setOnboardingSeen() {
        appPreferences.hasSeenOnboarding = true
    }
}