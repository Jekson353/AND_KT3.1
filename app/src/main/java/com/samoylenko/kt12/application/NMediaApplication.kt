package com.samoylenko.kt12.application

import android.app.Application
import com.samoylenko.kt12.auth.AppAuth

class NMediaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppAuth.initApp(this)
    }
}