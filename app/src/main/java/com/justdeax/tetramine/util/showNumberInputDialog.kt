package com.justdeax.tetramine.util

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.justdeax.tetramine.R
import com.justdeax.tetramine.databinding.DialogNumberInputBinding

fun Context.showNumberInputDialog(
    min: Number,
    max: Number,
    isFloat: Boolean,
    textId: Int,
    onResult: (Number) -> Unit
) {
    val binding = DialogNumberInputBinding.inflate(LayoutInflater.from(this))
    val inputLayout = binding.textInputLayout
    val editText = binding.editDecimalInput

    val dialog = MaterialAlertDialogBuilder(this)
        .setTitle(getString(R.string.set_value))
        .setView(binding.root)
        .setPositiveButton(getString(R.string.confirm), null)
        .setNegativeButton(getString(R.string.cancel), null)
        .create()

    dialog.setOnShowListener {
        val minVal = min.toDouble()
        val maxVal = max.toDouble()
        val text = if (isFloat)
            getString(textId, min.toFloat().toString(), max.toFloat().toString())
        else
            getString(textId, min.toInt().toString(), max.toInt().toString())

        inputLayout.hint = text
        editText.inputType = if (isFloat)
            android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        else
            android.text.InputType.TYPE_CLASS_NUMBER

        editText.requestFocus()
        editText.postDelayed({
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
        }, 100)

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val inputStr = editText.text?.toString()?.trim()
            val value = inputStr?.toDoubleOrNull()

            if (value == null || value < minVal || value > maxVal) {
                inputLayout.error = text
            } else {
                inputLayout.error = null
                dialog.dismiss()
                onResult(if (isFloat) value.toFloat() else value.toInt())
            }
        }
    }
    dialog.show()
}