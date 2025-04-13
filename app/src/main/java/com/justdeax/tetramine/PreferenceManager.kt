package com.justdeax.tetramine
import android.content.Context
import android.content.SharedPreferences

val Context.preference: SharedPreferences
    get() = getSharedPreferences("preference", Context.MODE_PRIVATE)

fun SharedPreferences.set(settings: SharedPreferences.Editor.() -> Unit): SharedPreferences.Editor =
    edit().apply { settings(); apply() }

object PreferenceManager {
    private const val FIRST_LAUNCH = "FIRST_LAUNCH"
    private const val THEME_MODE = "THEME_MODE"
    private const val THEME_DYNAMIC = "THEME_DYNAMIC"

    fun Context.setFirstLaunch(boolean: Boolean) =
        preference.set {
            putBoolean(FIRST_LAUNCH, boolean)
        }
    fun Context.getFirstLaunch() =
        preference.getBoolean(FIRST_LAUNCH, true)

    fun Context.setThemeMode(int: Int) =
        preference.set {
            putInt(THEME_MODE, int)
        }
    fun Context.getThemeMode() =
        preference.getInt(THEME_MODE, 0)

    fun Context.setThemeDynamic(boolean: Boolean) =
        preference.set {
            putBoolean(THEME_DYNAMIC, boolean)
        }
    fun Context.getThemeDynamic() =
        preference.getBoolean(THEME_MODE, true)

}