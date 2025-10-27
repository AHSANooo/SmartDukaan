package com.example.smartdukaan

import android.app.Application

class SmartDukaanApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize DataManager when app starts
        DataManager.init(applicationContext)
    }
}

