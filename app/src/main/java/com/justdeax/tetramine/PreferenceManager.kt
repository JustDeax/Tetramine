package com.justdeax.tetramine

import android.app.Activity
import android.content.Context
import androidx.core.content.edit
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

object PreferenceManager {
    private val Context.preferences get() = getSharedPreferences(NAME, Context.MODE_PRIVATE)
    private const val NAME = "preference"

    private const val KEY_FIRST_LAUNCH = "FL"
    private const val KEY_PRACTICE_LEVEL = "PL"

    private const val KEY_NIGHT_THEME_MODE = "NTM"
    private const val KEY_DYNAMIC_COLORS = "DC"
    private const val KEY_SHOW_GHOST_PIECE = "SGP"
    private const val KEY_EMPTY_CELL_OPACITY = "ECO"
    private const val KEY_CELL_CORNER_RADIUS = "CCR"
    private const val KEY_CELL_SPACING = "CS"
    private const val KEY_MUSIC = "M"
    private const val KEY_X_SENSITIVITY = "XS"
    private const val KEY_Y_SENSITIVITY = "YS"
    private const val KEY_MAX_TIME_HDT = "MTH"
    private const val KEY_MIN_SOFT_DROPS_HDT = "MSDH"
    private const val KEY_BI_DIRECTION_ROTATION = "BDR"

    private const val KEY_BEST_SCORE = "BS"
    private const val KEY_BEST_LINES = "BL"
    private const val KEY_BEST_PIECES = "BP"
    private const val KEY_BEST_4_LINES = "B4L"
    private const val KEY_BEST_T_SPINS = "BTS"
    private const val KEY_TOTAL_SCORE = "TS"
    private const val KEY_TOTAL_LINES = "TL"
    private const val KEY_TOTAL_PIECES = "TP"
    private const val KEY_TOTAL_4_LINES = "T4L"
    private const val KEY_TOTAL_T_SPINS = "TTS"

    val Activity.versionName
        get() = packageManager.getPackageInfo(packageName, 0).versionName!!

    var Context.isFirstLaunch by booleanPreference(KEY_FIRST_LAUNCH, true)
    var Context.practiceLevel by intPreference(KEY_PRACTICE_LEVEL, 0)
    var Context.isNightThemeMode by booleanPreference(KEY_NIGHT_THEME_MODE, true)
    var Context.isDynamicColors by booleanPreference(KEY_DYNAMIC_COLORS, true)
    var Context.isShowGhostPiece by booleanPreference(KEY_SHOW_GHOST_PIECE, true)
    var Context.emptyCellOpacity by floatPreference(KEY_EMPTY_CELL_OPACITY, 1f)
    var Context.cellCornerRadius by floatPreference(KEY_CELL_CORNER_RADIUS, 0.6f)
    var Context.cellSpacing by floatPreference(KEY_CELL_SPACING, 2f)
    var Context.isMusicEnable by booleanPreference(KEY_MUSIC, true)
    var Context.xSensitivity by floatPreference(KEY_X_SENSITIVITY, 0.9f)
    var Context.ySensitivity by floatPreference(KEY_Y_SENSITIVITY, 0.8f)
    var Context.maxTimeHDT by intPreference(KEY_MAX_TIME_HDT, 150)
    var Context.minSoftDropsHDT by intPreference(KEY_MIN_SOFT_DROPS_HDT, 3)
    var Context.is2DirectionRotation by booleanPreference(KEY_BI_DIRECTION_ROTATION, false)
    var Context.bestScore by intPreference(KEY_BEST_SCORE, 0)
    var Context.bestLines by intPreference(KEY_BEST_LINES, 0)
    var Context.bestPieces by intPreference(KEY_BEST_PIECES, 0)
    var Context.best4Lines by intPreference(KEY_BEST_4_LINES, 0)
    var Context.bestTSpins by intPreference(KEY_BEST_T_SPINS, 0)
    var Context.totalScore by intPreference(KEY_TOTAL_SCORE, 0)
    var Context.totalLines by intPreference(KEY_TOTAL_LINES, 0)
    var Context.totalPieces by intPreference(KEY_TOTAL_PIECES, 0)
    var Context.total4Lines by intPreference(KEY_TOTAL_4_LINES, 0)
    var Context.totalTSpins by intPreference(KEY_TOTAL_T_SPINS, 0)

    fun Context.resetSettings() {
        preferences.edit {
            remove(KEY_NIGHT_THEME_MODE)
            remove(KEY_DYNAMIC_COLORS)
            remove(KEY_EMPTY_CELL_OPACITY)
            remove(KEY_CELL_CORNER_RADIUS)
            remove(KEY_CELL_SPACING)
            remove(KEY_MUSIC)
            remove(KEY_X_SENSITIVITY)
            remove(KEY_Y_SENSITIVITY)
            remove(KEY_MAX_TIME_HDT)
            remove(KEY_MIN_SOFT_DROPS_HDT)
            remove(KEY_BI_DIRECTION_ROTATION)
            remove(KEY_SHOW_GHOST_PIECE)
        }
    }

    fun Context.resetStats() {
        preferences.edit {
            remove(KEY_BEST_SCORE)
            remove(KEY_BEST_LINES)
            remove(KEY_BEST_PIECES)
            remove(KEY_BEST_4_LINES)
            remove(KEY_BEST_T_SPINS)
            remove(KEY_TOTAL_SCORE)
            remove(KEY_TOTAL_LINES)
            remove(KEY_TOTAL_PIECES)
            remove(KEY_TOTAL_4_LINES)
            remove(KEY_TOTAL_T_SPINS)
        }
    }

    fun booleanPreference(key: String, defaultValue: Boolean) =
        object : ReadWriteProperty<Context, Boolean> {
            override fun getValue(thisRef: Context, property: KProperty<*>) =
                thisRef.preferences.getBoolean(key, defaultValue)
            override fun setValue(thisRef: Context, property: KProperty<*>, value: Boolean) =
                thisRef.preferences.edit { putBoolean(key, value) }
        }

    fun intPreference(key: String, defaultValue: Int) =
        object : ReadWriteProperty<Context, Int> {
            override fun getValue(thisRef: Context, property: KProperty<*>) =
                thisRef.preferences.getInt(key, defaultValue)
            override fun setValue(thisRef: Context, property: KProperty<*>, value: Int) =
                thisRef.preferences.edit { putInt(key, value) }
        }

    fun floatPreference(key: String, defaultValue: Float) =
        object : ReadWriteProperty<Context, Float> {
            override fun getValue(thisRef: Context, property: KProperty<*>) =
                thisRef.preferences.getFloat(key, defaultValue)
            override fun setValue(thisRef: Context, property: KProperty<*>, value: Float) =
                thisRef.preferences.edit { putFloat(key, value) }
        }
}