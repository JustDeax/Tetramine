package com.justdeax.tetramine.util
import androidx.activity.OnBackPressedCallback

fun createOnBackCallback(action: () -> Unit) = object : OnBackPressedCallback(true) {
    override fun handleOnBackPressed() = action()
}