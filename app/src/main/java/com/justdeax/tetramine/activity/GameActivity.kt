package com.justdeax.tetramine.activity

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
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
import com.justdeax.tetramine.PreferenceManager.xSensitivity
import com.justdeax.tetramine.PreferenceManager.ySensitivity
import com.justdeax.tetramine.R
import com.justdeax.tetramine.databinding.ActivityGameBinding
import com.justdeax.tetramine.databinding.DialogGameBinding
import com.justdeax.tetramine.game.TetramineGameFactory
import com.justdeax.tetramine.game.TetramineGameViewModel
import com.justdeax.tetramine.game.Tetromino
import com.justdeax.tetramine.popup.AchievementPopup
import com.justdeax.tetramine.popup.GuidePopup
import com.justdeax.tetramine.util.applySystemInsets
import com.justdeax.tetramine.util.constant.GameType
import com.justdeax.tetramine.util.getStatistics
import com.justdeax.tetramine.util.getTetrominoType
import com.justdeax.tetramine.util.onBackListener
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.abs

class GameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGameBinding
    private lateinit var dialogGameBinding: DialogGameBinding
    private lateinit var dialogGame: AlertDialog
    private val rows = 20
    private val cols = 10
    private val game: TetramineGameViewModel by viewModels {
        TetramineGameFactory(rows, cols) { text -> achievementPopup.show(text) }
    }
    private val guidePopup by lazy {
        GuidePopup(binding.root, lifecycleScope, this, layoutInflater)
    }
    private val achievementPopup by lazy {
        AchievementPopup(binding.root, lifecycleScope, layoutInflater)
    }

    private var colors = intArrayOf()
    private var boardColor = intArrayOf()
    private var previewColor = intArrayOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (resources.configuration.smallestScreenWidthDp < 600)
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        setupViews()

        when (intent.getStringExtra(GameType.TYPE)) {
            GameType.GUIDE -> {
                binding.main.post { showGuide(fullGuide = true) }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                game.board.collectLatest { newBoard ->
                    binding.board.update(newBoard)
                    binding.preview.update(game.previousPiece.shape)
                    binding.statistics.text = getStatistics(game.lines, game.score)
                    if (game.isGameOver)
                        showGameDialog()
                }
            }
        }
        game.resumeGame()
        onBackListener { showGameDialog() }
    }

    private fun setupViews() {
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
        }
    }

    private fun showGameDialog() {
        if (!game.isGameOver)
            game.stopGame()
        if (!::dialogGame.isInitialized || !::dialogGameBinding.isInitialized)
            initGameDialog()

        dialogGameBinding.apply {
            if (game.isGameOver) {
                preview.visibility = View.GONE
                gameOver.visibility = View.VISIBLE
                resume.text = getString(R.string.view_game)
                dialogGame.setOnDismissListener { }
            } else {
                preview.visibility = View.VISIBLE
                gameOver.visibility = View.GONE
                resume.text = getString(R.string.resume)
                dialogGame.setOnDismissListener { game.resumeGame() }
                preview.update(
                    Tetromino.TETROMINO_SHAPES[getTetrominoType(game.currentPiece.shape) - 1]
                )
            }

            statistics.text = getStatistics(game.lines, game.score)
            dialogGame.show()
        }
    }

    private fun initGameDialog() {
        dialogGameBinding = DialogGameBinding.inflate(layoutInflater)
        dialogGame = MaterialAlertDialogBuilder(this)
            .setView(dialogGameBinding.root)
            .create()

        dialogGameBinding.apply {
            preview.setStyle(previewColor + colors, cellSpacing, cellCornerRadius)
            resume.setOnClickListener {
                dialogGame.dismiss()
            }
            guide.setOnClickListener {
                dialogGame.dismiss()
                showGuide()
            }
            exit.setOnClickListener {
                dialogGame.dismiss()
                finish()
            }
            restart.setOnClickListener {
                dialogGame.dismiss()
                game.startGame()
            }
        }
        dialogGame.setCanceledOnTouchOutside(false)
        dialogGame.setOnCancelListener { finish() }
    }

    private fun showGuide(fullGuide: Boolean = false) {
        if (game.isGameOver)
            game.startGame()
        guidePopup.show(
            { game.currentPiece.col },
            { game.score },
            { hardDropCount },
            { rotateCount },
            { game.stopGame() },
            { game.resumeGame() },
            fullGuide
        )
    }

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
                        if (diffX > 0)
                            game.moveRight()
                        else
                            game.moveLeft()
                        xMotion++
                        lastTouchY = event.y
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