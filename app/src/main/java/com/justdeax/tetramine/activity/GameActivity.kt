package com.justdeax.tetramine.activity

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.ColorUtils
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.justdeax.tetramine.PreferenceManager.cellCornerRadius
import com.justdeax.tetramine.PreferenceManager.cellSpacing
import com.justdeax.tetramine.PreferenceManager.emptyCellOpacity
import com.justdeax.tetramine.PreferenceManager.is2DirectionRotation
import com.justdeax.tetramine.PreferenceManager.isMusicEnable
import com.justdeax.tetramine.PreferenceManager.maxTimeHDT
import com.justdeax.tetramine.PreferenceManager.minSoftDropsHDT
import com.justdeax.tetramine.PreferenceManager.practiceLevel
import com.justdeax.tetramine.PreferenceManager.xSensitivity
import com.justdeax.tetramine.PreferenceManager.ySensitivity
import com.justdeax.tetramine.R
import com.justdeax.tetramine.databinding.ActivityGameBinding
import com.justdeax.tetramine.databinding.DialogGameBinding
import com.justdeax.tetramine.game.TetramineGameFactory
import com.justdeax.tetramine.game.TetramineGameViewModel
import com.justdeax.tetramine.game.Tetromino
import com.justdeax.tetramine.util.applySystemInsets
import com.justdeax.tetramine.util.constant.GameMode
import com.justdeax.tetramine.util.constant.Text
import com.justdeax.tetramine.util.getStatistics
import com.justdeax.tetramine.util.getTetrominoType
import com.justdeax.tetramine.util.onBackListener
import com.justdeax.tetramine.window.AchievementPopup
import com.justdeax.tetramine.window.GuidePopup
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class GameActivity : BaseActivity() {
    private lateinit var binding: ActivityGameBinding
    private lateinit var dialogGameBinding: DialogGameBinding
    private lateinit var dialogGame: AlertDialog
    private val rows = 20
    private val cols = 10
    private val game: TetramineGameViewModel by viewModels {
        TetramineGameFactory(application, rows, cols) { text -> achievementPopup.show(text) }
    }
    private val guidePopup: GuidePopup by lazy {
        GuidePopup(binding.root, lifecycleScope, this, layoutInflater)
    }
    private val achievementPopup: AchievementPopup by lazy {
        AchievementPopup(binding.root, lifecycleScope, layoutInflater)
    }

    private var colors = intArrayOf()
    private var boardColor = intArrayOf()
    private var previewColor = intArrayOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @SuppressLint("SourceLockedOrientationActivity")
        if (resources.configuration.smallestScreenWidthDp < 600)
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        setupViews()
        setupGame(savedInstanceState == null)
        onBackListener { showGameDialog() }
    }

    private fun setupViews() {
        boardColor = intArrayOf(
            when (emptyCellOpacity) {
                1f -> getColor(R.color.empty)
                0f -> getColor(R.color.invisible)
                else -> ColorUtils.setAlphaComponent(
                    getColor(R.color.empty), (255 * emptyCellOpacity).toInt()
                )
            }
        )
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
            board.setControls(
                game, xSensitivity, ySensitivity, is2DirectionRotation, maxTimeHDT, minSoftDropsHDT
            )
            pause.setOnClickListener { showGameDialog() }
        }
    }

    private fun setupGame(isNewInstance: Boolean) {
        if (isNewInstance) {
            when (intent.getStringExtra(GameMode.MODE)) {
                GameMode.PRACTICE -> {
                    game.isLevelStatic = true
                    game.changeLevel(practiceLevel)
                }
                GameMode.GUIDE -> {
                    binding.main.post {
                        showGuide(fullGuide = true)
                    }
                }
            }
            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    launch {
                        game.board.collectLatest { newBoard ->
                            binding.board.update(newBoard)
                            binding.preview.update(game.previousPiece.shape)
                            binding.statistics.text = getStatistics(game.lines, game.score)
                            if (game.isGameOver)
                                showGameDialog()
                        }
                    }
                    launch {
                        game.level.collectLatest { newLevel ->
                            binding.pause.text =
                                if (newLevel == TetramineGameViewModel.levels.lastIndex) Text.SIGMA
                                else newLevel.toString()
                        }
                    }
                }
            }
        }
        game.resumeGame()
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
            music.setImageDrawable(getMusicImage())
            statistics.text = getStatistics(game.lines, game.score, game.level.value)
        }
        dialogGame.show()
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
                showGuide(fullGuide = false)
            }
            music.setOnClickListener {
                isMusicEnable = !isMusicEnable
                music.setImageDrawable(getMusicImage())
            }
            exit.setOnClickListener {
                dialogGame.setOnDismissListener {  }
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

    private fun getMusicImage(): Drawable? = AppCompatResources.getDrawable(this@GameActivity,
            if (isMusicEnable) R.drawable.round_music_note_24
            else R.drawable.round_music_off_24
        )

    private fun showGuide(fullGuide: Boolean) {
        if (game.isGameOver || game.score < 200)
            game.startGame()
        guidePopup.show(
            { game.currentPiece.col },
            { game.score },
            { binding.board.hardDropCount },
            { binding.board.rotateCount },
            { game.stopGame() },
            { game.resumeGame() },
            fullGuide
        )
    }

    override fun onPause() {
        if (!isFinishing && !isDestroyed)
            showGameDialog()
        super.onPause()
    }
}