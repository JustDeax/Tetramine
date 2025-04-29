package com.justdeax.tetramine.util

import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow

fun createPopupWindow(view: View) = PopupWindow(
    view,
    ViewGroup.LayoutParams.WRAP_CONTENT,
    ViewGroup.LayoutParams.WRAP_CONTENT
).apply {
    isOutsideTouchable = false
    isFocusable = false
    animationStyle = androidx.appcompat.R.style.Animation_AppCompat_DropDownUp
}