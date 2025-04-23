package com.justdeax.tetramine.util
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback

fun ComponentActivity.onBackListener(action: () -> Unit) {
    onBackPressedDispatcher.addCallback(
        this,
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() = action()
        }
    )
}