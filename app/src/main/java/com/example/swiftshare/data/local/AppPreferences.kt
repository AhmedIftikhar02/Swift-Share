package com.example.swiftshare.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AppPreferences @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("swiftshare_prefs", Context.MODE_PRIVATE)

    var hasSeenOnboarding: Boolean
        get() = prefs.getBoolean(KEY_ONBOARDING_SEEN, false)
        set(value) = prefs.edit { putBoolean(KEY_ONBOARDING_SEEN, value) }

    var deviceDisplayName: String?
        get() = prefs.getString(KEY_DEVICE_NAME, null)
        set(value) = prefs.edit { putString(KEY_DEVICE_NAME, value) }

    var autoAcceptEnabled: Boolean
        get() = prefs.getBoolean(KEY_AUTO_ACCEPT, false)
        set(value) = prefs.edit { putBoolean(KEY_AUTO_ACCEPT, value) }

    var defaultSaveLocation: String?
        get() = prefs.getString(KEY_SAVE_LOCATION, null)
        set(value) = prefs.edit { putString(KEY_SAVE_LOCATION, value) }

    private companion object {
        const val KEY_ONBOARDING_SEEN = "has_seen_onboarding"
        const val KEY_DEVICE_NAME = "device_display_name"
        const val KEY_AUTO_ACCEPT = "auto_accept_enabled"
        const val KEY_SAVE_LOCATION = "default_save_location"
    }
}