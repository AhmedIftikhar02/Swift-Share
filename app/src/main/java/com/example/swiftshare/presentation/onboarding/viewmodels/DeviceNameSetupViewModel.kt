package com.example.swiftshare.presentation.onboarding.viewmodels


import android.os.Build
import com.example.swiftshare.base.BaseViewModel
import com.example.swiftshare.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DeviceNameSetupViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : BaseViewModel() {

    val defaultDeviceName: String get() = Build.MODEL ?: "My Device"

    fun completeOnboarding(deviceName: String) = launchSafe {
        val finalName = deviceName.trim().ifBlank { defaultDeviceName }
        settingsRepository.setDeviceDisplayName(finalName)
        settingsRepository.setOnboardingSeen()
    }
}