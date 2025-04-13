package com.justdeax.tetramine
import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.color.DynamicColors
import com.justdeax.tetramine.PreferenceManager.getThemeDynamic
import com.justdeax.tetramine.PreferenceManager.getThemeMode

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        val theme = getThemeMode()
        val dynamic = getThemeDynamic()
        AppCompatDelegate.setDefaultNightMode(
            when (theme) {
                1 -> AppCompatDelegate.MODE_NIGHT_NO
                2 -> AppCompatDelegate.MODE_NIGHT_YES
                else -> AppCompatDelegate.MODE_NIGHT_YES
            }
        )
        if (dynamic) {
            DynamicColors.applyToActivitiesIfAvailable(this)
            setTheme(R.style.Theme_Dynamic)
        } else {
            setTheme(R.style.Theme_Static)
        }
    }
}
