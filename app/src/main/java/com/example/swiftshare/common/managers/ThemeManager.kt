package com.example.swiftshare.common.managers

import androidx.appcompat.app.AppCompatDelegate
import javax.inject.Inject
import javax.inject.Singleton

enum class ThemeMode { LIGHT, DARK, SYSTEM }

/**
 * Lets the app override system dark/light mode with an in-app setting (e.g. a Settings screen
 * toggle). Call applyTheme() once at app startup (in MyApplication.onCreate) with the saved
 * preference, and again any time the user changes it.
 *
 * This does NOT require a 3rd themes.xml - see the long comment in values/themes.xml for why.
 * AppCompatDelegate.setDefaultNightMode forces which of values/themes.xml or
 * values-night/themes.xml gets resolved, exactly as if the user changed system settings.
 */
@Singleton
class ThemeManager @Inject constructor(
    private val sharedPrefsManager: SharedPrefsManager
) {
    fun applyTheme(mode: ThemeMode) {
        sharedPrefsManager.putString(SharedPrefsManager.THEME_MODE, mode.name)
        val nightMode = when (mode) {
            ThemeMode.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            ThemeMode.DARK -> AppCompatDelegate.MODE_NIGHT_YES
            ThemeMode.SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        AppCompatDelegate.setDefaultNightMode(nightMode)
    }

    fun getSavedTheme(): ThemeMode {
        val saved = sharedPrefsManager.getString(SharedPrefsManager.THEME_MODE)
        return ThemeMode.entries.find { it.name == saved } ?: ThemeMode.SYSTEM
    }
}
