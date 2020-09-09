package com.example.testapplication

import android.app.Application
import timber.log.Timber

class MainApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        setLogger()
    }

    private fun setLogger() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

    }
}