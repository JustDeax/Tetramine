package com.justdeax.tetramine
import android.content.Context
import android.content.SharedPreferences

object PreferenceManager {
    private val Context.preferences get() = getSharedPreferences(NAME, Context.MODE_PRIVATE)
    private const val NAME = "preference"
    private const val KEY_FIRST_LAUNCH = "FIRST_LAUNCH_1"
    private const val KEY_THEME_MODE = "THEME_MODE"
    private const val KEY_THEME_DYNAMIC = "THEME_DYNAMIC"
    private const val KEY_CELL_SPACING = "CELL_SPACING"
    private const val KEY_CELL_CORNER_RADIUS = "CELL_CORNER_RADIUS"

    private inline fun SharedPreferences.set(settings: SharedPreferences.Editor.() -> Unit) {
        edit().apply {
            settings()
            apply()
        }
    }

    var Context.isFirstLaunch: Boolean
        get() = preferences.getBoolean(KEY_FIRST_LAUNCH, true)
        set(value) = preferences.set { putBoolean(KEY_FIRST_LAUNCH, value) }

    var Context.themeMode: Int
        get() = preferences.getInt(KEY_THEME_MODE, 0)
        set(value) = preferences.set { putInt(KEY_THEME_MODE, value) }

    var Context.isThemeDynamic: Boolean
        get() = preferences.getBoolean(KEY_THEME_DYNAMIC, true)
        set(value) = preferences.set { putBoolean(KEY_THEME_DYNAMIC, value) }

    var Context.cellSpacing: Int
        get() = preferences.getInt(KEY_CELL_SPACING, 2)
        set(value) = preferences.set { putInt(KEY_CELL_SPACING, value) }

    var Context.cellCornerRadius: Int
        get() = preferences.getInt(KEY_CELL_CORNER_RADIUS, 8)
        set(value) = preferences.set { putInt(KEY_CELL_CORNER_RADIUS, value) }
}