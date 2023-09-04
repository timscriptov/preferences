package com.mcal.preferences.template

import android.app.Application
import com.mcal.preferences.Preferences

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Preferences(filesDir, "preferences.data").init()
    }
}