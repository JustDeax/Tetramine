package com.justdeax.tetramine.popup

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.lifecycle.LifecycleCoroutineScope
import com.justdeax.tetramine.databinding.PopupAchievementBinding
import com.justdeax.tetramine.util.constant.Delay
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AchievementPopup(
    private val anchorView: View,
    private val lifecycleScope: LifecycleCoroutineScope,
    layoutInflater: LayoutInflater
) {
    private val binding = PopupAchievementBinding.inflate(layoutInflater)
    private val popup = PopupWindow(
        binding.root,
        ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    ).apply {
        isOutsideTouchable = false
        isFocusable = false
        animationStyle = androidx.appcompat.R.style.Animation_AppCompat_DropDownUp
    }
    private var job: Job? = null

    fun show(text: String) {
        if (text.isEmpty()) return

        job?.cancel()
        popup.dismiss()

        job = lifecycleScope.launch {
            try {
                popup.showAtLocation(anchorView, Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 0)
                repeat(text.length) { i ->
                    binding.textView.text = text.substring(0, i + 1)
                    delay(Delay.TYPING_LETTER)
                }
                delay(Delay.ACHIEVEMENT_VISIBLE)
                popup.dismiss()
            } finally {
                popup.dismiss()
            }
        }
    }
}