package com.justdeax.tetramine

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences

object PreferenceManager {
    private val Context.preferences get() = getSharedPreferences(NAME, Context.MODE_PRIVATE)
    private const val NAME = "preference"
    private const val ZERO = 0

    private const val KEY_FIRST_LAUNCH = "FL"
    private const val KEY_NIGHT_THEME_MODE = "NTM"
    private const val KEY_THEME_DYNAMIC = "TD"
    private const val KEY_CELL_SPACING = "CS"
    private const val KEY_CELL_CORNER_RADIUS = "CCR"
    private const val KEY_X_SENSITIVITY = "XS"
    private const val KEY_Y_SENSITIVITY = "YS"
    private const val KEY_USE_ROTATE_LEFT = "URL"

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

    private const val KEY_GAME_DATA_SCORE = "GS"
    private const val KEY_GAME_DATA_LINES = "GL"
    private const val KEY_GAME_DATA_PIECES = "GP"
    private const val KEY_GAME_DATA_4_LINES = "G4L"
    private const val KEY_GAME_DATA_T_SPINS = "GTS"

    val Activity.versionName: String
        get() = " " + packageManager.getPackageInfo(packageName, 0).versionName!!

    var Context.isFirstLaunch: Boolean
        get() = preferences.getBoolean(KEY_FIRST_LAUNCH, true)
        set(value) = preferences.set { putBoolean(KEY_FIRST_LAUNCH, value) }

    var Context.isNightThemeMode: Boolean
        get() = preferences.getBoolean(KEY_NIGHT_THEME_MODE, true)
        set(value) = preferences.set { putBoolean(KEY_NIGHT_THEME_MODE, value) }

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

    var Context.useRotateLeft: Boolean
        get() = preferences.getBoolean(KEY_USE_ROTATE_LEFT, false)
        set(value) = preferences.set { putBoolean(KEY_USE_ROTATE_LEFT, value) }

    var Context.bestScore: Int
        get() = preferences.getInt(KEY_BEST_SCORE, ZERO)
        set(value) = preferences.set { putInt(KEY_BEST_SCORE, value) }

    var Context.bestLines: Int
        get() = preferences.getInt(KEY_BEST_LINES, ZERO)
        set(value) = preferences.set { putInt(KEY_BEST_LINES, value) }

    var Context.bestPieces: Int
        get() = preferences.getInt(KEY_BEST_PIECES, ZERO)
        set(value) = preferences.set { putInt(KEY_BEST_PIECES, value) }

    var Context.best4Lines: Int
        get() = preferences.getInt(KEY_BEST_4_LINES, ZERO)
        set(value) = preferences.set { putInt(KEY_BEST_4_LINES, value) }

    var Context.bestTSpins: Int
        get() = preferences.getInt(KEY_BEST_T_SPINS, ZERO)
        set(value) = preferences.set { putInt(KEY_BEST_T_SPINS, value) }

    var Context.totalScore: Int
        get() = preferences.getInt(KEY_TOTAL_SCORE, ZERO)
        set(value) = preferences.set { putInt(KEY_TOTAL_SCORE, value) }

    var Context.totalLines: Int
        get() = preferences.getInt(KEY_TOTAL_LINES, ZERO)
        set(value) = preferences.set { putInt(KEY_TOTAL_LINES, value) }

    var Context.totalPieces: Int
        get() = preferences.getInt(KEY_TOTAL_PIECES, ZERO)
        set(value) = preferences.set { putInt(KEY_TOTAL_PIECES, value) }

    var Context.total4Lines: Int
        get() = preferences.getInt(KEY_TOTAL_4_LINES, ZERO)
        set(value) = preferences.set { putInt(KEY_TOTAL_4_LINES, value) }

    var Context.totalTSpins: Int
        get() = preferences.getInt(KEY_TOTAL_T_SPINS, ZERO)
        set(value) = preferences.set { putInt(KEY_TOTAL_T_SPINS, value) }

    var Context.gameDataScore: Int
        get() = preferences.getInt(KEY_GAME_DATA_SCORE, ZERO)
        set(value) = preferences.set { putInt(KEY_GAME_DATA_SCORE, value) }

    var Context.gameDataLines: Int
        get() = preferences.getInt(KEY_GAME_DATA_LINES, ZERO)
        set(value) = preferences.set { putInt(KEY_GAME_DATA_LINES, value) }

    var Context.gameDataPieces: Int
        get() = preferences.getInt(KEY_GAME_DATA_PIECES, ZERO)
        set(value) = preferences.set { putInt(KEY_GAME_DATA_PIECES, value) }

    var Context.gameData4Lines: Int
        get() = preferences.getInt(KEY_GAME_DATA_4_LINES, ZERO)
        set(value) = preferences.set { putInt(KEY_GAME_DATA_4_LINES, value) }

    var Context.gameDataTSpins: Int
        get() = preferences.getInt(KEY_GAME_DATA_T_SPINS, ZERO)
        set(value) = preferences.set { putInt(KEY_GAME_DATA_T_SPINS, value) }

    fun Context.resetStats() {
        preferences.set {
            putInt(KEY_BEST_SCORE, ZERO)
            putInt(KEY_BEST_LINES, ZERO)
            putInt(KEY_BEST_PIECES, ZERO)
            putInt(KEY_BEST_4_LINES, ZERO)
            putInt(KEY_BEST_T_SPINS, ZERO)
            putInt(KEY_TOTAL_SCORE, ZERO)
            putInt(KEY_TOTAL_LINES, ZERO)
            putInt(KEY_TOTAL_PIECES, ZERO)
            putInt(KEY_TOTAL_4_LINES, ZERO)
            putInt(KEY_TOTAL_T_SPINS, ZERO)
        }
    }

    fun Context.resetGameData() {
        preferences.set {
            putInt(KEY_GAME_DATA_SCORE, ZERO)
            putInt(KEY_GAME_DATA_LINES, ZERO)
            putInt(KEY_GAME_DATA_PIECES, ZERO)
            putInt(KEY_GAME_DATA_4_LINES, ZERO)
            putInt(KEY_GAME_DATA_T_SPINS, ZERO)
        }
    }

    private inline fun SharedPreferences.set(settings: SharedPreferences.Editor.() -> Unit) {
        edit().apply {
            settings()
            apply()
        }
    }
}