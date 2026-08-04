package com.example.swiftshare.common.managers

import androidx.appcompat.app.AppCompatDelegate
import javax.inject.Inject
import javax.inject.Singleton

enum class ThemeMode { LIGHT, DARK, SYSTEM }
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
