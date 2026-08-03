package com.example.swiftshare.common.extensions

import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment

fun View.visible() { visibility = View.VISIBLE }
fun View.gone() { visibility = View.GONE }
fun View.invisible() { visibility = View.INVISIBLE }
fun View.visibleIf(condition: Boolean) { visibility = if (condition) View.VISIBLE else View.GONE }

/**
 * Debounced click listener - prevents double-navigation/double-submit bugs from a user
 * double-tapping a button before the first click's action (e.g. navigation) completes.
 */
fun View.setDebouncedClickListener(debounceMs: Long = 600L, action: () -> Unit) {
    var lastClickTime = 0L
    setOnClickListener {
        val now = System.currentTimeMillis()
        if (now - lastClickTime >= debounceMs) {
            lastClickTime = now
            action()
        }
    }
}

fun Fragment.toast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}

fun String.cleanDeviceName(): String {
    return this
        .replace("::PHONE::", "")
        .replace("::TABLET::", "")
        .replace("::UNKNOWN::", "")
        .trim()
}
