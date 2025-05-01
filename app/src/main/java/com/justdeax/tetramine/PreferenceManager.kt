package com.justdeax.tetramine

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences

object PreferenceManager {
    private val Context.preferences get() = getSharedPreferences(NAME, Context.MODE_PRIVATE)
    private const val NAME = "preference"
    private const val KEY_FIRST_LAUNCH = "KEY_FIRST_LAUNCH"
    private const val KEY_THEME_MODE = "KEY_THEME_MODE"
    private const val KEY_THEME_DYNAMIC = "KEY_THEME_DYNAMIC"
    private const val KEY_CELL_SPACING = "KEY_CELL_SPACING"
    private const val KEY_CELL_CORNER_RADIUS = "KEY_CELL_CORNER_RADIUS"
    private const val KEY_X_SENSITIVITY = "KEY_X_SENSITIVITY"
    private const val KEY_Y_SENSITIVITY = "KEY_Y_SENSITIVITY"

    var Context.isFirstLaunch: Boolean
        get() = preferences.getBoolean(KEY_FIRST_LAUNCH, true)
        set(value) = preferences.set { putBoolean(KEY_FIRST_LAUNCH, value) }

    var Context.themeMode: Int
        get() = preferences.getInt(KEY_THEME_MODE, 0)
        set(value) = preferences.set { putInt(KEY_THEME_MODE, value) }

    var Context.isThemeDynamic: Boolean
        get() = preferences.getBoolean(KEY_THEME_DYNAMIC, true)
        set(value) = preferences.set { putBoolean(KEY_THEME_DYNAMIC, value) }

    var Context.cellSpacing: Float
        get() = preferences.getFloat(KEY_CELL_SPACING, 2f)
        set(value) = preferences.set { putFloat(KEY_CELL_SPACING, value) }

    var Context.cellCornerRadius: Float
        get() = preferences.getFloat(KEY_CELL_CORNER_RADIUS, 8f)
        set(value) = preferences.set { putFloat(KEY_CELL_CORNER_RADIUS, value) }

    var Context.xSensitivity: Float
        get() = preferences.getFloat(KEY_X_SENSITIVITY, 0.9f)
        set(value) = preferences.set { putFloat(KEY_X_SENSITIVITY, value) }

    var Context.ySensitivity: Float
        get() = preferences.getFloat(KEY_Y_SENSITIVITY, 0.8f)
        set(value) = preferences.set { putFloat(KEY_Y_SENSITIVITY, value) }

    private inline fun SharedPreferences.set(settings: SharedPreferences.Editor.() -> Unit) {
        edit().apply {
            settings()
            apply()
        }
    }

    val Activity.versionName: String
        get() = " " + packageManager.getPackageInfo(packageName, 0).versionName!!
}