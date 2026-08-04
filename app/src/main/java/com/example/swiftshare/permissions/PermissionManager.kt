package com.example.swiftshare.permissions

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PermissionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun isGranted(type: PermissionType): Boolean {
        if (Build.VERSION.SDK_INT !in type.minSdk..type.maxSdk) return true
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