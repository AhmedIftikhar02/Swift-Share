package com.example.swiftshare.core.util

import timber.log.Timber

class ReleaseTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority == android.util.Log.WARN || priority == android.util.Log.ERROR) {
            // TODO: forward to your crash reporting tool of choice.
        }
    }
}