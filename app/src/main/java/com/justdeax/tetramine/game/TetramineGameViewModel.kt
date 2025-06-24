package com.justdeax.tetramine.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TetramineGameViewModel(
    rows: Int,
    cols: Int,
    showAchievement: (String) -> Unit
) : ViewModel() {
    private val tetramine = Tetramine(rows, cols, showAchievement) { level.value + 1 }
    private var dropSpeed = levels[0].speed
    private var gameJob: Job? = null
    var isLevelStatic = false

    val currentPiece get() = tetramine.currentPiece
    val previousPiece get() = tetramine.previousPiece
    val isGameOver get() = tetramine.isGameOver
    val score get() = tetramine.score
    val lines get() = tetramine.lines

    val pieces get() = tetramine.pieces
    val fourLines get() = tetramine.fourLines
    val tSpins get() = tetramine.tSpins

    private val _board = MutableStateFlow(tetramine.getBoardWithPiece())
    val board = _board.asStateFlow()

    private val _level = MutableStateFlow(0)
    val level = _level.asStateFlow()

    fun startGame() {
        resetGame()
        resumeGame()
    }

    fun resumeGame() {
        if (!tetramine.isGameOver && gameJob == null)
            gameJob = viewModelScope.launch(Dispatchers.Default) {
                while (!tetramine.isGameOver) {
                    delay(dropSpeed)
                    withContext(Dispatchers.Main) {
                        tetramine.dropPiece()
                        _board.value = tetramine.getBoardWithPiece()
                        if (!isLevelStatic) {
                            val nextLevel = levels.getOrNull(level.value + 1)
                            if (nextLevel != null && lines >= nextLevel.lines)
                                changeLevel(level.value + 1)
                        }
                    }
                }
                withContext(Dispatchers.Main) {
                    stopGame()
                }
            }
    }

    fun stopGame() {
        gameJob?.cancel()
        gameJob = null
        _board.value = tetramine.getBoardWithPiece()
    }

    fun resetGame() {
        gameJob?.cancel()
        gameJob = null
        tetramine.resetGame()
        _board.value = tetramine.getBoardWithPiece()

        if (!isLevelStatic)
            changeLevel(0)
    }

    fun changeLevel(level: Int) {
        require(level in levels.indices)
        _level.value = level
        dropSpeed = levels[level].speed
    }

    fun moveLeft() {
        gameAction { tetramine.moveLeft() }
    }

    fun moveRight() {
        gameAction { tetramine.moveRight() }
    }

    fun rotateLeft() {
        gameAction { tetramine.rotateLeft() }
    }

    fun rotateRight() {
        gameAction { tetramine.rotateRight() }
    }

    fun hardDrop() {
        gameAction { tetramine.hardDrop() }
    }

    fun softDrop() {
        gameAction { tetramine.softDrop() }
    }

    private fun gameAction(action: () -> Unit) {
        if (!tetramine.isGameOver) {
            action()
            _board.value = tetramine.getBoardWithPiece()
        }
    }

    override fun onCleared() {
        stopGame()
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