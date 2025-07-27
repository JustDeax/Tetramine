package com.justdeax.tetramine.game

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.justdeax.tetramine.PreferenceManager.best4Lines
import com.justdeax.tetramine.PreferenceManager.bestLines
import com.justdeax.tetramine.PreferenceManager.bestPieces
import com.justdeax.tetramine.PreferenceManager.bestScore
import com.justdeax.tetramine.PreferenceManager.bestTSpins
import com.justdeax.tetramine.PreferenceManager.isShowGhostPiece
import com.justdeax.tetramine.PreferenceManager.total4Lines
import com.justdeax.tetramine.PreferenceManager.totalLines
import com.justdeax.tetramine.PreferenceManager.totalPieces
import com.justdeax.tetramine.PreferenceManager.totalScore
import com.justdeax.tetramine.PreferenceManager.totalTSpins
import com.justdeax.tetramine.util.constant.Delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TetramineGameViewModel(
    private val application: Application,
    private val rows: Int,
    private val cols: Int,
    private val showAchievement: (String) -> Unit
) : AndroidViewModel(application) {
    private var tetramine = makeGame()
    private var dropSpeed = levels[0].speed
    private var gameJob: Job? = null
    private val music = MusicManager(application, Delay.LONG)
    var isLevelStatic = false

    private val _board = MutableStateFlow(tetramine.getBoardWithPiece())
    val board = _board.asStateFlow()

    private val _level = MutableStateFlow(0)
    val level = _level.asStateFlow()

    val currentPiece get() = tetramine.currentPiece
    val previousPiece get() = tetramine.previousPiece
    val isGameOver get() = tetramine.isGameOver
    val score get() = tetramine.score
    val lines get() = tetramine.lines

    fun startGame() {
        resetGame()
        resumeGame()
    }

    fun resumeGame() {
        if (!tetramine.isGameOver && gameJob == null) {
            gameJob = viewModelScope.launch(Dispatchers.Default) {
                while (!tetramine.isGameOver) {
                    delay(dropSpeed)
                    gameTick()
                }
                withContext(Dispatchers.Main) {
                    stopGame()
                    saveGame()
                }
            }
            music.play(level.value)
        }
    }

    private suspend fun gameTick() {
        withContext(Dispatchers.Main) {
            tetramine.dropPiece()
            _board.value = tetramine.getBoardWithPiece()
            if (!isLevelStatic) {
                val nextLevel = levels.getOrNull(level.value + 1)
                if (nextLevel != null && lines >= nextLevel.lines) {
                    changeLevel(level.value + 1)
                    music.play(level.value)
                }
            }
        }
    }

    fun stopGame() {
        gameJob?.cancel()
        gameJob = null
        music.pause()
    }

    fun resetGame() {
        stopGame()
        saveGame()
        tetramine = makeGame()
        _board.value = tetramine.getBoardWithPiece()
        music.release()

        if (!isLevelStatic)
            changeLevel(0)
    }

    fun changeLevel(level: Int) {
        require(level in levels.indices)
        _level.value = level
        dropSpeed = levels[level].speed
    }

    fun moveLeft() = gameAction { tetramine.moveLeft() }

    fun moveRight() = gameAction { tetramine.moveRight() }

    fun rotateLeft() = gameAction { tetramine.rotateLeft() }

    fun rotateRight() = gameAction { tetramine.rotateRight() }

    fun hardDrop() = gameAction { tetramine.hardDrop() }

    fun softDrop() = gameAction { tetramine.softDrop() }

    private fun makeGame() = Tetramine(rows, cols, showAchievement, application.isShowGhostPiece) { level.value + 1 }

    private fun saveGame() {
        val pieces = tetramine.pieces
        val fourLines = tetramine.fourLines
        val tSpins = tetramine.tSpins

        with(application) {
            if (score > bestScore) bestScore = score
            if (lines > bestLines) bestLines = lines
            if (pieces > bestPieces) bestPieces = pieces
            if (fourLines > best4Lines) best4Lines = fourLines
            if (tSpins > bestTSpins) bestTSpins = tSpins

            totalScore += score
            totalLines += lines
            totalPieces += pieces
            total4Lines += fourLines
            totalTSpins += tSpins
        }
    }

    private fun gameAction(action: () -> Unit) {
        if (!tetramine.isGameOver) {
            action()
            _board.value = tetramine.getBoardWithPiece()
        }
    }

    override fun onCleared() {
        saveGame()
        music.release()
        super.onCleared()
    }

    companion object {
        data class Level(val speed: Long, val lines: Int)

        private infix fun Long.to(that: Int) = Level(this, that)

        val levels = arrayOf(
            800L to 0,   // 0
            750L to 10,  // 1
            700L to 20,  // 2
            650L to 35,  // 3
            600L to 50,  // 4
            550L to 70,  // 5
            500L to 90,  // 6
            450L to 110, // 7
            400L to 130, // 8
            350L to 150, // 9
            320L to 175, // 10
            300L to 200, // 11
            280L to 230, // 12
            260L to 260, // 13
            240L to 295, // 14
            220L to 330, // 15
            200L to 370, // 16
            180L to 410, // 17
            160L to 455, // 18
            140L to 505, // 19
            120L to 555, // 20
        )
    }
}