package com.justdeax.tetramine.window

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.justdeax.tetramine.R

fun Context.showSimpleDialog(titleRes: Int, action: () -> Unit) {
    val dialog = MaterialAlertDialogBuilder(this)
        .setTitle(titleRes)
        .setPositiveButton(R.string.confirm) { _, _ -> action()}
        .create()
    dialog.show()
}