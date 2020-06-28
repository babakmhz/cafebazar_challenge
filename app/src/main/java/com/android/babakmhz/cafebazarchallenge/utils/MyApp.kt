package com.android.babakmhz.cafebazarchallenge.utils

import android.app.Application
import com.android.babakmhz.cafebazarchallenge.BuildConfig

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            AppLogger.init()
        }
    }
}