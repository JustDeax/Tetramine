package com.justdeax.tetramine.util

import android.os.Build
import android.window.OnBackInvokedDispatcher
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback

fun ComponentActivity.onBackListener(action: () -> Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        onBackInvokedDispatcher.registerOnBackInvokedCallback(
            OnBackInvokedDispatcher.PRIORITY_DEFAULT, action
        )
    } else {
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) { override fun handleOnBackPressed() = action() }
        )
    }
}