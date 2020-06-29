package com.android.babakmhz.cafebazarchallenge.utils

import com.android.babakmhz.cafebazarchallenge.BuildConfig
import com.android.babakmhz.cafebazarchallenge.di.component.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

class MyApp : DaggerApplication() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            AppLogger.init()
        }
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder().create(this)
    }
}