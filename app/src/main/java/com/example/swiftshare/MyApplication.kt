package com.example.swiftshare

import android.app.Application
import com.example.swiftshare.common.managers.ThemeManager
import com.example.swiftshare.core.util.ReleaseTree
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class MyApplication : Application() {

    @Inject lateinit var themeManager: ThemeManager

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.ENABLE_LOGGING) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(ReleaseTree())
        }

        themeManager.applyTheme(themeManager.getSavedTheme())
    }
}