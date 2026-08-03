package com.example.swiftshare.permissions


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

/** Routes to this app's system Settings page — used when a permission is permanently
 *  denied and can no longer be requested via the in-app dialog (PRD Section 12, "If Denied"). */
object AppSettingsNavigator {
    fun openAppSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }
}