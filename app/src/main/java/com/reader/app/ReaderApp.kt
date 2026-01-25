package com.reader.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ReaderApp : Application() {
    
    override fun onCreate() {
        super.onCreate()
    }
}
