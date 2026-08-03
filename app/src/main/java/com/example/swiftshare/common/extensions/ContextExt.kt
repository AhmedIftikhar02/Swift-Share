package com.example.swiftshare.common.extensions

import android.content.Context
import android.content.res.Configuration
import android.widget.Toast

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.isDarkModeOn(): Boolean {
    val mode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
    return mode == Configuration.UI_MODE_NIGHT_YES
}

fun Context.dpToPx(dp: Float): Float = dp * resources.displayMetrics.density
