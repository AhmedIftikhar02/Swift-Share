package com.example.swiftshare.common.providers

import android.content.Context
import androidx.annotation.StringRes
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Why this exists: ViewModels should not hold a direct reference to Activity/Fragment Context
 * (lifecycle/leak risk, and makes unit testing harder). But they often legitimately need a
 * string resource for an error message. Injecting ApplicationContext (safe, no leak since it
 * lives as long as the app) through this thin wrapper is the standard professional pattern -
 * and in tests you just provide a fake ResourceProvider instead of mocking Context.
 */
@Singleton
class ResourceProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun getString(@StringRes resId: Int): String = context.getString(resId)
    fun getString(@StringRes resId: Int, vararg args: Any): String = context.getString(resId, *args)
}
