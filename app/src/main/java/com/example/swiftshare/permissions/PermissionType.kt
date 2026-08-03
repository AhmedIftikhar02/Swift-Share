package com.example.swiftshare.permissions


import android.Manifest
import android.os.Build

/**
 * One entry per permission group from PRD Section 12, with the exact API range it applies
 * to baked in — call sites never scatter their own Build.VERSION checks.
 */
enum class PermissionType(
    val manifestPermissions: List<String>,
    val minSdk: Int,
    val maxSdk: Int = Int.MAX_VALUE,
    /** True = discovery/connection cannot function at all without it (requested at
     *  onboarding). False = requested contextually later (e.g. notifications, PRD 2.15). */
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
        maxSdk = Build.VERSION_CODES.S_V2, // API 29–32, per PRD 12's "up to API 32" OEM note
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
        isCritical = false // requested just before the first transfer, not at onboarding
    );

    companion object {
        /** The set requested during onboarding (PRD 4.1) — every critical permission
         *  applicable to the device's current API level. */
        fun onboardingSet(): List<PermissionType> =
            entries.filter { it.isCritical && Build.VERSION.SDK_INT in it.minSdk..it.maxSdk }
    }
}