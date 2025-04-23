package com.justdeax.tetramine

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.color.DynamicColors
import com.justdeax.tetramine.PreferenceManager.isThemeDynamic
import com.justdeax.tetramine.PreferenceManager.themeMode

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(
            when (themeMode) {
                1 -> AppCompatDelegate.MODE_NIGHT_NO
                2 -> AppCompatDelegate.MODE_NIGHT_YES
                else -> AppCompatDelegate.MODE_NIGHT_YES
            }
        )
        if (isThemeDynamic) {
            DynamicColors.applyToActivitiesIfAvailable(this)
            setTheme(R.style.Theme_Dynamic)
        } else {
            setTheme(R.style.Theme_Static)
        }
    }
}
