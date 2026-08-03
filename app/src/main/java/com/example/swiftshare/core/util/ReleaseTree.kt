package com.example.swiftshare.core.util

import timber.log.Timber

/** Debug tree logs everything; Release tree only logs WARN/ERROR — extension point for a
 *  crash-reporting tool if you add one later (Firebase Crashlytics, Sentry, etc.). */
class ReleaseTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority == android.util.Log.WARN || priority == android.util.Log.ERROR) {
            // TODO: forward to your crash reporting tool of choice.
        }
    }
}