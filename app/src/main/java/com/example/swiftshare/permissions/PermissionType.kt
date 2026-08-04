package com.example.swiftshare.permissions


import android.Manifest
import android.os.Build


enum class PermissionType(
    val manifestPermissions: List<String>,
    val minSdk: Int,
    val maxSdk: Int = Int.MAX_VALUE,
    val isCritical: Boolean
) {
    BLUETOOTH_SCAN(
        manifestPermissions = listOf(Manifest.permission.BLUETOOTH_SCAN),
        minSdk = Build.VERSION_CODES.S,
        isCritical = true
    ),
    BLUETOOTH_ADVERTISE(
        manifestPermissions = listOf(Manifest.permission.BLUETOOTH_ADVERTISE),
        minSdk = Build.VERSION_CODES.S,
        isCritical = true
    ),
    BLUETOOTH_CONNECT(
        manifestPermissions = listOf(Manifest.permission.BLUETOOTH_CONNECT),
        minSdk = Build.VERSION_CODES.S,
        isCritical = true
    ),
    LEGACY_LOCATION(
        manifestPermissions = listOf(Manifest.permission.ACCESS_FINE_LOCATION),
        minSdk = Build.VERSION_CODES.Q,
        maxSdk = Build.VERSION_CODES.S_V2,
        isCritical = true
    ),
    NEARBY_WIFI_DEVICES(
        manifestPermissions = listOf(Manifest.permission.NEARBY_WIFI_DEVICES),
        minSdk = Build.VERSION_CODES.TIRAMISU,
        isCritical = true
    ),
    NOTIFICATIONS(
        manifestPermissions = listOf(Manifest.permission.POST_NOTIFICATIONS),
        minSdk = Build.VERSION_CODES.TIRAMISU,
        isCritical = false
    );

    companion object {

        fun onboardingSet(): List<PermissionType> =
            entries.filter { it.isCritical && Build.VERSION.SDK_INT in it.minSdk..it.maxSdk }
    }
}