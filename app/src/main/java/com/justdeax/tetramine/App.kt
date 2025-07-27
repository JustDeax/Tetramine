package com.justdeax.tetramine

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.justdeax.tetramine.PreferenceManager.isNightThemeMode

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(
            if (isNightThemeMode)
                AppCompatDelegate.MODE_NIGHT_YES
            else
                AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}
