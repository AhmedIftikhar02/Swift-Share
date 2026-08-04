package com.example.swiftshare.common.extensions

import android.app.Activity
import android.view.View
import androidx.core.view.updatePadding

fun Activity.enableEdgeToEdge() {
    androidx.core.view.WindowCompat.setDecorFitsSystemWindows(window, false)
}


fun View.applySystemBarInsetsAsPadding(
    consumeTop: Boolean = true,
    consumeBottom: Boolean = true,
    onInsetsApplied: (top: Int, bottom: Int) -> Unit = { _, _ -> }
) {
    androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val bars = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars())
        view.updatePadding(
            top = if (consumeTop) bars.top else view.paddingTop,
            bottom = if (consumeBottom) bars.bottom else view.paddingBottom
        )
        onInsetsApplied(bars.top, bars.bottom)
        insets
    }
    requestApplyInsetsWhenAttached()
}

private fun View.requestApplyInsetsWhenAttached() {
    if (isAttachedToWindow) {
        androidx.core.view.ViewCompat.requestApplyInsets(this)
    } else {
        addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                v.removeOnAttachStateChangeListener(this)
                androidx.core.view.ViewCompat.requestApplyInsets(v)
            }
            override fun onViewDetachedFromWindow(v: View) = Unit
        })
    }
}