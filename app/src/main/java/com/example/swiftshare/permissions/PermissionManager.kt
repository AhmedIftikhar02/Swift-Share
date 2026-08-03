package com.example.swiftshare.permissions

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Central, typed permission-state checker. Every screen asks THIS instead of calling
 * ContextCompat.checkSelfPermission directly, so the API-level gating logic in
 * PermissionType.kt lives in exactly one place.
 */
@Singleton
class PermissionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun isGranted(type: PermissionType): Boolean {
        if (Build.VERSION.SDK_INT !in type.minSdk..type.maxSdk) return true // N/A on this OS version
        return type.manifestPermissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun pendingOnboardingPermissions(): List<PermissionType> =
        PermissionType.onboardingSet().filterNot { isGranted(it) }

    fun allCriticalPermissionsGranted(): Boolean =
        PermissionType.onboardingSet().all { isGranted(it) }

    fun manifestPermissionsFor(types: List<PermissionType>): Array<String> =
        types.flatMap { it.manifestPermissions }.distinct().toTypedArray()
}