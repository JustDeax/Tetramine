package com.justdeax.tetramine.util

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.justdeax.tetramine.R

fun Context.showResetDialog(titleRes: Int, resetAction: () -> Unit) {
    val dialog = MaterialAlertDialogBuilder(this)
        .setTitle(titleRes)
        .setPositiveButton(R.string.confirm) { _, _ -> resetAction }
        .setNegativeButton(R.string.cancel) { _, _ -> }
        .create()
    dialog.show()
}