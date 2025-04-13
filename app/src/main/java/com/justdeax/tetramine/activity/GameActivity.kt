package com.justdeax.tetramine.activity
import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupWindow
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.justdeax.tetramine.PreferenceManager.getFirstLaunch
import com.justdeax.tetramine.PreferenceManager.setFirstLaunch
import com.justdeax.tetramine.R
import com.justdeax.tetramine.databinding.ActivityGameBinding
import com.justdeax.tetramine.databinding.CustomBannerBinding
import com.justdeax.tetramine.databinding.DialogGameBinding
import com.justdeax.tetramine.game.TetramineGameFactory
import com.justdeax.tetramine.game.TetramineGameViewModel
import com.justdeax.tetramine.game.Tetromino
import com.justdeax.tetramine.util.applySystemInsets
import com.justdeax.tetramine.util.findNumber
import com.justdeax.tetramine.util.padArray2x4
import com.justdeax.tetramine.util.setStatistics
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
        TetramineGameFactory(rows, cols, ::makeBanner)
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
            getColor(R.color.magenta), //T
            getColor(R.color.orange), //L
            getColor(R.color.blue), //J
            getColor(R.color.green), //S
            getColor(R.color.red), //Z
            getColor(R.color.ghost)
        )

        binding.apply {
            main.applySystemInsets()
            board.setColors(boardColor + colors)
            preview.setColors(previewColor + colors)
            board.setControls()
            pause.setOnClickListener { showPauseDialog() }

            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    game.board.collectLatest { newBoard ->
                        board.updateBoard(newBoard)
                        preview.updateBoard(padArray2x4(game.previousPiece.shape))
                        statistics.text = setStatistics(game.lines, game.score)
                        if (game.isGameOver) showGameOver()
                    }
                }
            }
            game.resumeGame()
        }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() = showPauseDialog()
        })
        if (getFirstLaunch()) {
            showHelpDialog()
            setFirstLaunch(false)
        }
    }

    private fun showPauseDialog() {
        game.stopGame()
        if (dialogGameBinding == null || dialogGame == null)
            initGameDialog()
        dialogGameBinding?.apply {
            val tetrominoShape = findNumber(game.currentPiece.shape) - 1
            preview.setColors(previewColor + colors)
            preview.updateBoard(
                padArray2x4(Tetromino.TETROMINO_SHAPES[tetrominoShape])
            )
            statistics.text = setStatistics(game.lines, game.score)
            dialogGame?.show()
        }
    }

    private fun showGameOver() {
        if (dialogGameBinding == null || dialogGame == null)
            initGameDialog()
        dialogGameBinding?.apply {
            val gameOverText = "GAME OVER"
            gameOver.text = gameOverText
            resume.text = getString(R.string.view_game)
            preview.visibility = View.GONE
            gameOver.visibility = View.VISIBLE
            statistics.text = setStatistics(game.lines, game.score)
            dialogGame?.setOnDismissListener { dialogGame = null }
            dialogGame?.show()
        }
    }

    private fun initGameDialog() {
        dialogGameBinding = DialogGameBinding.inflate(layoutInflater)
        dialogGame = MaterialAlertDialogBuilder(this)
            .setView(dialogGameBinding?.root)
            .setOnDismissListener { game.resumeGame() }
            .create()

        dialogGameBinding?.apply {
            resume.setOnClickListener {
                dialogGame?.dismiss()
            }
            help.setOnClickListener {
                showHelpDialog()
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
    }

    private fun showHelpDialog() {
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.dialog_help, null)
        val gifView = view.findViewById<ImageView>(R.id.gif_view)

        Glide.with(this)
            .load(R.drawable.help_with_controllers)
            .into(gifView)

        MaterialAlertDialogBuilder(this)
            .setView(view)
            .setPositiveButton("ОК", null)
            .show()
    }

    private fun makeBanner(text: String) {
        val bannerBinding = CustomBannerBinding.inflate(layoutInflater)
        bannerBinding.textView.text = text

        val popup = PopupWindow(
            bannerBinding.root,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            isOutsideTouchable = true
            isFocusable = false
            elevation = 10f
        }

        popup.showAtLocation(binding.root, Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 300)
        Handler(Looper.getMainLooper()).postDelayed({
            popup.dismiss()
        }, 2000)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun View.setControls() {
        val xSensitivity = 0.55
        val ySensitivity = 0.40

        var lastTouchX = 0f
        var lastTouchY = 0f
        var xMotion = 0
        var yMotion = 0
        var motionTime = 0L

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
                    val thresholdX = width / (cols / 2.0) * xSensitivity
                    val thresholdY = height / (rows / 2.0) * ySensitivity

                    if (abs(diffX) > thresholdX && abs(diffY) < thresholdY) {
                        if (diffX > 3) {
                            game.moveRight()
                            xMotion++
                        } else if (diffX < -3) {
                            game.moveLeft()
                            xMotion++
                        }
                        lastTouchX = event.x
                    } else if (diffY > thresholdY) {
                        if (game.currentPiece.row > 0)
                            game.softDrop()
                        yMotion++
                        lastTouchY = event.y
                    }
                }
                MotionEvent.ACTION_UP -> {
                    val diffTime = System.currentTimeMillis() - motionTime
                    if (
                        xMotion == 0 && yMotion > 2 &&
                        diffTime in 20L until 200L &&
                        game.currentPiece.row > 3
                    ) {
                        game.hardDrop()
                    } else if (xMotion == 0 && yMotion == 0) {
                        game.rotateRight()
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
        showPauseDialog()
        super.onStop()
    }
}