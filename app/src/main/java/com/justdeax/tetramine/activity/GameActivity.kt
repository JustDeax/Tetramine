package com.justdeax.tetramine.activity

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.Gravity
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.justdeax.tetramine.PreferenceManager.cellCornerRadius
import com.justdeax.tetramine.PreferenceManager.cellSpacing
import com.justdeax.tetramine.PreferenceManager.isFirstLaunch
import com.justdeax.tetramine.PreferenceManager.xSensitivity
import com.justdeax.tetramine.PreferenceManager.ySensitivity
import com.justdeax.tetramine.R
import com.justdeax.tetramine.databinding.ActivityGameBinding
import com.justdeax.tetramine.databinding.BannerAchievementBinding
import com.justdeax.tetramine.databinding.BannerGuideBinding
import com.justdeax.tetramine.databinding.DialogGameBinding
import com.justdeax.tetramine.game.TetramineGameFactory
import com.justdeax.tetramine.game.TetramineGameViewModel
import com.justdeax.tetramine.game.Tetromino
import com.justdeax.tetramine.util.GameType
import com.justdeax.tetramine.util.applySystemInsets
import com.justdeax.tetramine.util.getTetrominoType
import com.justdeax.tetramine.util.getStatistics
import com.justdeax.tetramine.util.onBackListener
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.abs

class GameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGameBinding
    private var dialogGameBinding: DialogGameBinding? = null
    private var dialogGame: AlertDialog? = null

    private var colors = intArrayOf()
    private var boardColor = intArrayOf()
    private var previewColor = intArrayOf()

    private val rows = 20
    private val cols = 10
    private val game: TetramineGameViewModel by viewModels {
        TetramineGameFactory(rows, cols, ::showAchievement)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (resources.configuration.smallestScreenWidthDp < 600)
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        boardColor = intArrayOf(getColor(R.color.empty))
        previewColor = intArrayOf(getColor(R.color.invisible))
        colors = intArrayOf(
            getColor(R.color.cyan), //I
            getColor(R.color.yellow), //O
            getColor(R.color.purple), //T
            getColor(R.color.orange), //L
            getColor(R.color.blue), //J
            getColor(R.color.green), //S
            getColor(R.color.red), //Z
            getColor(R.color.ghost)
        )

        binding.apply {
            main.applySystemInsets()
            board.setStyle(boardColor + colors, cellSpacing, cellCornerRadius)
            preview.setStyle(previewColor + colors, cellSpacing, cellCornerRadius)
            board.setControls(xSensitivity, ySensitivity)
            pause.setOnClickListener { showGameDialog() }

            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    game.board.collectLatest { newBoard ->
                        board.update(newBoard)
                        preview.update(game.previousPiece.shape)
                        statistics.text = getStatistics(game.lines, game.score)
                        if (game.isGameOver) showGameDialog()
                    }
                }
            }

            when (intent.getStringExtra(GameType.TYPE)) {
                GameType.CLASSIC -> { }
                GameType.PRACTICE -> { }
                GameType.SPRINT -> { }
                GameType.MODERN -> { }
                GameType.GUIDE -> {
                    main.post { showGuide() }
                    game.enableStaticSpeed(1000)
                }
            }

            game.resumeGame()
        }
        onBackListener { showGameDialog() }
    }

    private fun showGameDialog() {
        if (!game.isGameOver)
            game.stopGame()
        if (dialogGame == null)
            initGameDialog()

        dialogGameBinding?.apply {
            if (game.isGameOver) {
                preview.visibility = View.GONE
                gameOver.visibility = View.VISIBLE
                resume.text = getString(R.string.view_game)
                dialogGame?.setOnDismissListener { dialogGame = null }
            } else {
                preview.update(
                    Tetromino.TETROMINO_SHAPES[getTetrominoType(game.currentPiece.shape) - 1]
                )
                dialogGame?.setOnDismissListener { game.resumeGame() }
            }

            statistics.text = getStatistics(game.lines, game.score)
            dialogGame?.show()
        }
    }

    private fun initGameDialog() {
        dialogGameBinding = DialogGameBinding.inflate(layoutInflater)
        dialogGame = MaterialAlertDialogBuilder(this)
            .setView(dialogGameBinding?.root)
            .create()

        dialogGameBinding?.apply {
            preview.setStyle(previewColor + colors, cellSpacing, cellCornerRadius)
            resume.setOnClickListener {
                dialogGame?.dismiss()
            }
            guide.setOnClickListener {
                dialogGame?.dismiss()
                showGuide()
            }
            exit.setOnClickListener {
                dialogGame?.dismiss()
                finish()
            }
            restart.setOnClickListener {
                dialogGame?.dismiss()
                game.startGame()
            }
        }

        dialogGame?.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                finish()
                true
            } else {
                false
            }
        }
    }

    private fun showAchievement(text: String) {
        val bannerBinding = BannerAchievementBinding.inflate(layoutInflater)
        val popup = PopupWindow(
            bannerBinding.root,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            isOutsideTouchable = false
            isFocusable = false
            animationStyle = androidx.appcompat.R.style.Animation_AppCompat_DropDownUp
        }

        lifecycleScope.launch {
            popup.showAtLocation(binding.root, Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 0)
            repeat(text.length) { i ->
                bannerBinding.textView.text = text.substring(0, i + 1)
                delay(25)
            }
            delay(1400)
            popup.dismiss()
        }
    }

    private fun showGuide() {
        val bannerBinding = BannerGuideBinding.inflate(layoutInflater)
        val popup = PopupWindow(
            bannerBinding.root,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            isOutsideTouchable = false
            isFocusable = false
            animationStyle = androidx.appcompat.R.style.Animation_AppCompat_DropDownUp
        }

        lifecycleScope.launch {
            val view = bannerBinding.textView
            val fullGuide = intent.getStringExtra(GameType.TYPE) == GameType.GUIDE
            var skip = false
            if (fullGuide) {
                view.setOnClickListener { skip = true }
                popup.showGuide {
                    view.text = makeGuideText(R.string.about_1)
                    skip = false
                    while (!skip)
                        delay(100)
                }
                popup.showGuide {
                    view.text = makeGuideText(R.string.about_2)
                    skip = false
                    while (!skip)
                        delay(100)
                }
            }
            popup.showGuide {
                view.text = getString(R.string.guide_1)
                val currentPieceColState = game.currentPiece.col
                while (abs(currentPieceColState - game.currentPiece.col) < 2)
                    delay(100)

                val newCurrentPieceColState = game.currentPiece.col
                if (currentPieceColState - game.currentPiece.col >= 2)
                    while (newCurrentPieceColState - game.currentPiece.col > -2)
                        delay(100)
                else
                    while (newCurrentPieceColState - game.currentPiece.col < 2)
                        delay(100)
            }
            popup.showGuide {
                view.text = getString(R.string.guide_2)
                val gameScoreState = game.score
                while (game.score < gameScoreState + 5)
                    delay(100)
            }
            popup.showGuide {
                view.text = getString(R.string.guide_3)
                val hardDropCountState = hardDropCount
                while (hardDropCount <= hardDropCountState)
                    delay(100)
            }
            popup.showGuide {
                view.text = getString(R.string.guide_4)
                val rotateCountState = rotateCount
                while (rotateCount <= rotateCountState)
                    delay(100)
            }
            if (fullGuide) {
                isFirstLaunch = false
                popup.showGuide {
                    view.text = makeGuideText(R.string.guide_5)
                    skip = false
                    while (!skip)
                        delay(100)
                }
            }
        }
    }

    private suspend fun PopupWindow.showGuide(action: suspend () -> Unit) {
        showAtLocation(binding.root, Gravity.END or Gravity.CENTER_VERTICAL, 0, 0)
        action()
        delay(500)
        dismiss()
        delay(500)
    }

    private fun makeGuideText(textId: Int) = getString(textId) + "\n= OK ="

    var hardDropCount = 0
    var rotateCount = 0
    private fun View.setControls(xSensitivity: Float, ySensitivity: Float) {
        var lastTouchX = 0f
        var lastTouchY = 0f
        var xMotion = 0
        var yMotion = 0
        var motionTime = 0L

        @SuppressLint("ClickableViewAccessibility")
        setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    lastTouchX = event.x
                    lastTouchY = event.y
                    motionTime = System.currentTimeMillis()
                }
                MotionEvent.ACTION_MOVE -> {
                    val diffX = event.x - lastTouchX
                    val diffY = event.y - lastTouchY
                    val thresholdX = width / cols * xSensitivity
                    val thresholdY = height / rows * ySensitivity

                    if (abs(diffX) > thresholdX && abs(diffY) < thresholdY) {
                        if (diffX > 0) {
                            game.moveRight()
                            xMotion++
                        } else {
                            game.moveLeft()
                            xMotion++
                        }
                        lastTouchX = event.x
                    } else if (diffY > thresholdY) {
                        if (game.currentPiece.row > 0) {
                            game.softDrop()
                            yMotion++
                        }
                        lastTouchY = event.y
                    }
                }
                MotionEvent.ACTION_UP -> {
                    val diffTime = System.currentTimeMillis() - motionTime
                    if (xMotion == 0 && yMotion > 2 && diffTime < 150L && game.currentPiece.row > 3) {
                        game.hardDrop()
                        hardDropCount++
                    } else if (xMotion == 0 && yMotion == 0) {
                        game.rotateRight()
                        rotateCount++
                    }

                    lastTouchX = 0f
                    lastTouchY = 0f
                    xMotion = 0
                    yMotion = 0
                }
            }
            true
        }
    }

    override fun onStop() {
        showGameDialog()
        super.onStop()
    }
}