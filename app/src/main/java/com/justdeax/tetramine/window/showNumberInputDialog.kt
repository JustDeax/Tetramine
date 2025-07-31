package com.justdeax.tetramine.window

import android.app.AlertDialog
import android.content.Context
import android.text.InputType
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.justdeax.tetramine.R
import com.justdeax.tetramine.databinding.DialogNumberInputBinding

fun Context.showNumberInputDialog(
    title: String,
    hintId: Int,
    min: Number,
    max: Number,
    isFloat: Boolean,
    default: Number = 0,
    onResult: (Number) -> Unit
) {
    val binding = DialogNumberInputBinding.inflate(LayoutInflater.from(this))
    val inputLayout = binding.textInputLayout
    val editText = binding.editDecimalInput
    val dialog = MaterialAlertDialogBuilder(this)
        .setTitle(title)
        .setView(binding.root)
        .setPositiveButton(getString(R.string.confirm), null)
        .setNegativeButton(getString(R.string.cancel), null)
        .create()

    dialog.setOnShowListener {
        val minVal = min.toDouble()
        val maxVal = max.toDouble()
        val hint = if (isFloat)
            getString(hintId, min.toFloat().toString(), max.toFloat().toString())
        else
            getString(hintId, min.toInt().toString(), max.toInt().toString())

        inputLayout.hint = hint
        editText.inputType = if (isFloat)
            InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        else
            InputType.TYPE_CLASS_NUMBER

        if (default != 0) {
            editText.setText((if (isFloat) default.toFloat() else default.toInt()).toString())
            editText.setSelection(editText.text?.length ?: 0)
        }

        editText.requestFocus()
        editText.postDelayed({
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
        }, 100)

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val value = editText.text?.toString()?.trim()?.toDoubleOrNull()
            if (value == null || value < minVal || value > maxVal) {
                inputLayout.error = hint
            } else {
                inputLayout.error = null
                dialog.dismiss()
                onResult(if (isFloat) value.toFloat() else value.toInt())
            }
        }
    }
    dialog.show()
}