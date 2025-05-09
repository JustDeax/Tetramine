package com.justdeax.tetramine.popup

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.lifecycle.LifecycleCoroutineScope
import com.justdeax.tetramine.PreferenceManager.isFirstLaunch
import com.justdeax.tetramine.R
import com.justdeax.tetramine.databinding.PopupGuideBinding
import com.justdeax.tetramine.util.constant.Delay
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

class GuidePopup(
    private val anchorView: View,
    private val lifecycleScope: LifecycleCoroutineScope,
    private val context: Context,
    layoutInflater: LayoutInflater
) {
    private val binding = PopupGuideBinding.inflate(layoutInflater)
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

    fun show(
        currentPieceCol: () -> Int,
        gameScore: () -> Int,
        hardDropCount: () -> Int,
        rotateCount: () -> Int,
        gameStop: () -> Unit,
        gameResume: () -> Unit,
        fullGuide: Boolean
    ) {
        job?.cancel()
        popup.dismiss()

        var skipRequested = false
        binding.root.setOnClickListener { skipRequested = true }

        job = lifecycleScope.launch {
            try {
                if (fullGuide) {
                    delay(Delay.SHORT)
                    gameStop()
                    skipRequested = false
                    showGuide({ skipRequested }, R.string.about_1, ok = true)
                    skipRequested = false
                    showGuide({ skipRequested }, R.string.about_2, ok = true)
                    gameResume()
                }

                var movedLeft = false
                var movedRight = false
                var initialCol = currentPieceCol()
                val hasMovedBothSides = {
                    val currentCol = currentPieceCol()
                    if (abs(initialCol - currentCol) >= 2) {
                        if (initialCol > currentCol)
                            movedLeft = true
                        else
                            movedRight = true
                        initialCol = currentCol
                    }
                    movedLeft && movedRight
                }

                showGuide(hasMovedBothSides, R.string.guide_1)

                val scoreStart = gameScore()
                showGuide({ gameScore() > scoreStart + 5 }, R.string.guide_2)

                val dropStart = hardDropCount()
                showGuide({ hardDropCount() > dropStart }, R.string.guide_3)

                val rotateStart = rotateCount()
                showGuide({ rotateCount() > rotateStart }, R.string.guide_4)

                if (fullGuide) {
                    context.isFirstLaunch = false
                    gameStop()
                    skipRequested = false
                    showGuide({ skipRequested }, R.string.guide_5, ok = true)
                    gameResume()
                }
            } finally {
                popup.dismiss()
            }
        }
    }

    private suspend fun showGuide(conditions: () -> Boolean, textRes: Int, ok: Boolean = false) {
        if (anchorView.isAttachedToWindow)
            popup.showAtLocation(anchorView, Gravity.END or Gravity.CENTER_VERTICAL, 0, 0)

        binding.textView.text = if (ok)
            context.getString(textRes) + "\n( OK )"
        else
            context.getString(textRes)

        while (!conditions())
            delay(Delay.MINI)
        delay(Delay.MINI)
        popup.dismiss()
        delay(Delay.MEDIUM)
    }
}