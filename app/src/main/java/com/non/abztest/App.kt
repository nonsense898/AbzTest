package com.non.abztest

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {
    override fun onTerminate() {
        super.onTerminate()
        Log.w("TAG", "onTerminate: ")
    }

    override fun onCreate() {
        super.onCreate()
    }
}