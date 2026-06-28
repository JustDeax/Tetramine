package com.justdeax.tetramine.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.color.DynamicColors
import com.justdeax.tetramine.PreferenceManager.isDynamicColors
import com.justdeax.tetramine.R

open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val isExtraDarkTheme = true
        if (isExtraDarkTheme) {
            setTheme(R.style.Theme_ExtraDark)
        }
//        if (isDynamicColors) {
//            DynamicColors.applyToActivitiesIfAvailable(this)
//            setTheme(R.style.Theme_Dynamic)
//        } else {
//            setTheme(R.style.Theme_Static)
//        }
        super.onCreate(savedInstanceState)
    }
}