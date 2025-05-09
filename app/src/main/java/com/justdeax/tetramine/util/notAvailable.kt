package com.justdeax.tetramine.util

import android.content.Context
import android.widget.Toast
import com.justdeax.tetramine.R

fun notAvailable(context: Context, mode: String) {
    Toast.makeText(
        context,
        mode + " " + context.getString(R.string.not_available),
        Toast.LENGTH_SHORT
    ).show()
}