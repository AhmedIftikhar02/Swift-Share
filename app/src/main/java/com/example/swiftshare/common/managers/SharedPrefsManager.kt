package com.example.swiftshare.common.managers

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Classic SharedPreferences wrapper, for general app settings that aren't security-sensitive
 * session data (that's SessionManager, which uses DataStore - see core/session/SessionManager.kt).
 *
 * Use this for things like: "has the user seen onboarding", "selected theme mode",
 * "selected language", feature flags toggled locally, last-synced timestamp, etc.
 */
@Singleton
class SharedPrefsManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun putString(key: String, value: String) = prefs.edit().putString(key, value).apply()
    fun getString(key: String, default: String? = null): String? = prefs.getString(key, default)

    fun putBoolean(key: String, value: Boolean) = prefs.edit().putBoolean(key, value).apply()
    fun getBoolean(key: String, default: Boolean = false): Boolean = prefs.getBoolean(key, default)

    fun putInt(key: String, value: Int) = prefs.edit().putInt(key, value).apply()
    fun getInt(key: String, default: Int = 0): Int = prefs.getInt(key, default)

    fun putLong(key: String, value: Long) = prefs.edit().putLong(key, value).apply()
    fun getLong(key: String, default: Long = 0L): Long = prefs.getLong(key, default)

    fun remove(key: String) = prefs.edit().remove(key).apply()
    fun clear() = prefs.edit().clear().apply()

    companion object Keys {
        const val HAS_SEEN_ONBOARDING = "has_seen_onboarding"
        const val THEME_MODE = "theme_mode" // see common/managers/ThemeManager.kt
        const val SELECTED_LANGUAGE = "selected_language"
    }
}
