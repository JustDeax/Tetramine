package com.justdeax.tetramine.activity

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.navigation.findNavController
import com.justdeax.tetramine.PreferenceManager.isFirstLaunch
import com.justdeax.tetramine.R
import com.justdeax.tetramine.databinding.ActivityMainBinding
import com.justdeax.tetramine.util.applySystemInsets
import com.justdeax.tetramine.util.constant.Delay
import com.justdeax.tetramine.util.constant.GameMode

class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding
    private var screenHeight = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @SuppressLint("SourceLockedOrientationActivity")
        if (resources.configuration.smallestScreenWidthDp < 600)
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        setupViews(savedInstanceState == null)
        checkOnFirstLaunch()
    }

    private fun setupViews(isNewInstance: Boolean) {
        binding.apply {
            main.applySystemInsets()
            main.post {
                screenHeight = main.height
                if (isNewInstance) {
                    animateHeightChange(logoLayout, screenHeight / 3)
                } else {
                    logoLayout.layoutParams.height = screenHeight / 3
                    logoLayout.requestLayout()
                }
            }
        }
    }

    private fun checkOnFirstLaunch() {
        if (isFirstLaunch) {
            val game = Intent(this, GameActivity::class.java)
            game.putExtra(GameMode.MODE, GameMode.GUIDE)
            startActivity(game)
        }
    }

    override fun onStart() {
        super.onStart()

        val navController = findNavController(R.id.nav_host_fragment)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.mainMenu) {
                animateHeightChange(binding.logoLayout, screenHeight / 3)
                binding.title.visibility = View.GONE
            } else {
                animateHeightToWrapContent(binding.logoLayout)
                binding.title.visibility = View.VISIBLE
            }

            binding.title.text = when (destination.id) {
                R.id.chooseMode -> getString(R.string.choose_mode)
                R.id.statistics -> getString(R.string.statistics)
                R.id.settings -> getString(R.string.settings)
                R.id.aboutGame -> getString(R.string.about_1)
                else -> ""
            }
        }
    }

    private fun animateHeightChange(view: View, newHeight: Int) {
        if (newHeight == 0) return

        val startHeight = view.height
        val animator = ValueAnimator.ofInt(startHeight, newHeight)

        animator.addUpdateListener { valueAnimator ->
            view.layoutParams.height = valueAnimator.animatedValue as Int
            view.requestLayout()
        }
        animator.duration = Delay.SHORT
        animator.start()
    }

    private fun animateHeightToWrapContent(view: View) {
        view.measure(
            View.MeasureSpec.makeMeasureSpec(view.width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        animateHeightChange(view, view.measuredHeight)
    }
}