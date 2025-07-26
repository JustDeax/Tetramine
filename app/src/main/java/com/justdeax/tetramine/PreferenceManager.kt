package com.justdeax.tetramine

import android.app.Activity
import android.content.Context
import androidx.core.content.edit

object PreferenceManager {
    private val Context.preferences get() = getSharedPreferences(NAME, Context.MODE_PRIVATE)
    private const val NAME = "preference"

    private const val KEY_FIRST_LAUNCH = "FL"

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

    val Activity.versionName: String
        get() = " " + packageManager.getPackageInfo(packageName, 0).versionName!!

    var Context.isFirstLaunch: Boolean
        get() = preferences.getBoolean(KEY_FIRST_LAUNCH, true)
        set(value) = preferences.edit { putBoolean(KEY_FIRST_LAUNCH, value) }

    var Context.isNightThemeMode: Boolean
        get() = preferences.getBoolean(KEY_NIGHT_THEME_MODE, true)
        set(value) = preferences.edit { putBoolean(KEY_NIGHT_THEME_MODE, value) }

    var Context.isDynamicColors: Boolean
        get() = preferences.getBoolean(KEY_DYNAMIC_COLORS, true)
        set(value) = preferences.edit { putBoolean(KEY_DYNAMIC_COLORS, value) }

    var Context.isShowGhostPiece: Boolean
        get() = preferences.getBoolean(KEY_SHOW_GHOST_PIECE, true)
        set(value) = preferences.edit { putBoolean(KEY_SHOW_GHOST_PIECE, value) }

    var Context.emptyCellOpacity: Float
        get() = preferences.getFloat(KEY_EMPTY_CELL_OPACITY, 1f)
        set(value) = preferences.edit { putFloat(KEY_EMPTY_CELL_OPACITY, value) }

    var Context.cellCornerRadius: Float
        get() = preferences.getFloat(KEY_CELL_CORNER_RADIUS, 0.6f)
        set(value) = preferences.edit { putFloat(KEY_CELL_CORNER_RADIUS, value) }

    var Context.cellSpacing: Float
        get() = preferences.getFloat(KEY_CELL_SPACING, 2f)
        set(value) = preferences.edit { putFloat(KEY_CELL_SPACING, value) }

    var Context.isMusicEnable: Boolean
        get() = preferences.getBoolean(KEY_MUSIC, true)
        set(value) = preferences.edit { putBoolean(KEY_MUSIC, value) }

    var Context.xSensitivity: Float
        get() = preferences.getFloat(KEY_X_SENSITIVITY, 0.9f)
        set(value) = preferences.edit { putFloat(KEY_X_SENSITIVITY, value) }

    var Context.ySensitivity: Float
        get() = preferences.getFloat(KEY_Y_SENSITIVITY, 0.8f)
        set(value) = preferences.edit { putFloat(KEY_Y_SENSITIVITY, value) }

    var Context.maxTimeHDT: Int
        get() = preferences.getInt(KEY_MAX_TIME_HDT, 150)
        set(value) = preferences.edit { putInt(KEY_MAX_TIME_HDT, value) }

    var Context.minSoftDropsHDT: Int
        get() = preferences.getInt(KEY_MIN_SOFT_DROPS_HDT, 3)
        set(value) = preferences.edit { putInt(KEY_MIN_SOFT_DROPS_HDT, value) }

    var Context.is2DirectionRotation: Boolean
        get() = preferences.getBoolean(KEY_BI_DIRECTION_ROTATION, false)
        set(value) = preferences.edit { putBoolean(KEY_BI_DIRECTION_ROTATION, value) }

    var Context.bestScore: Int
        get() = preferences.getInt(KEY_BEST_SCORE, 0)
        set(value) = preferences.edit { putInt(KEY_BEST_SCORE, value) }

    var Context.bestLines: Int
        get() = preferences.getInt(KEY_BEST_LINES, 0)
        set(value) = preferences.edit { putInt(KEY_BEST_LINES, value) }

    var Context.bestPieces: Int
        get() = preferences.getInt(KEY_BEST_PIECES, 0)
        set(value) = preferences.edit { putInt(KEY_BEST_PIECES, value) }

    var Context.best4Lines: Int
        get() = preferences.getInt(KEY_BEST_4_LINES, 0)
        set(value) = preferences.edit { putInt(KEY_BEST_4_LINES, value) }

    var Context.bestTSpins: Int
        get() = preferences.getInt(KEY_BEST_T_SPINS, 0)
        set(value) = preferences.edit { putInt(KEY_BEST_T_SPINS, value) }

    var Context.totalScore: Int
        get() = preferences.getInt(KEY_TOTAL_SCORE, 0)
        set(value) = preferences.edit { putInt(KEY_TOTAL_SCORE, value) }

    var Context.totalLines: Int
        get() = preferences.getInt(KEY_TOTAL_LINES, 0)
        set(value) = preferences.edit { putInt(KEY_TOTAL_LINES, value) }

    var Context.totalPieces: Int
        get() = preferences.getInt(KEY_TOTAL_PIECES, 0)
        set(value) = preferences.edit { putInt(KEY_TOTAL_PIECES, value) }

    var Context.total4Lines: Int
        get() = preferences.getInt(KEY_TOTAL_4_LINES, 0)
        set(value) = preferences.edit { putInt(KEY_TOTAL_4_LINES, value) }

    var Context.totalTSpins: Int
        get() = preferences.getInt(KEY_TOTAL_T_SPINS, 0)
        set(value) = preferences.edit { putInt(KEY_TOTAL_T_SPINS, value) }

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
}