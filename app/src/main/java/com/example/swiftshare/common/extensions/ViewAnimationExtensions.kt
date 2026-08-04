package com.example.swiftshare.common.extensions


import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.core.view.animation.PathInterpolatorCompat

fun View.withPressBounce(): View {
    val downInterpolator = PathInterpolatorCompat.create(0.4f, 0f, 0.2f, 1f)
    setOnTouchListener { v, event ->
        when (event.actionMasked) {
            android.view.MotionEvent.ACTION_DOWN -> {
                v.animate().scaleX(0.96f).scaleY(0.96f).setDuration(100)
                    .setInterpolator(downInterpolator).start()
            }
            android.view.MotionEvent.ACTION_UP, android.view.MotionEvent.ACTION_CANCEL -> {
                v.animate().scaleX(1f).scaleY(1f).setDuration(150)
                    .setInterpolator(OvershootInterpolator(2f)).start()
            }
        }
        false // don't consume — let the normal click listener still fire
    }
    return this
}