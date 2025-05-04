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
    private val tetramine = Tetramine(rows, cols, showAchievement) { level.value }
    private var dropSpeed = levels[0].speed
    private var isLevelStatic = false
    private var gameJob: Job? = null

    val currentPiece get() = tetramine.currentPiece
    val previousPiece get() = tetramine.previousPiece
    val isGameOver get() = tetramine.isGameOver
    val lines get() = tetramine.lines
    val score get() = tetramine.score

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

    fun setStaticSpeed(level: Int) {
        isLevelStatic = true
        changeLevel(level)
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

    private fun changeLevel(level: Int) {
        require(level in levels.indices)
        _level.value = level
        dropSpeed = levels[level].speed
    }

    override fun onCleared() {
        stopGame()
        super.onCleared()
    }

    companion object {
//        val levels = arrayOf(
//            800 to 0,   // 0
//            750 to 10,  // 1
//            700 to 20,  // 2
//            650 to 35,  // 3
//            600 to 50,  // 4
//            550 to 70,  // 5
//            500 to 90,  // 6
//            450 to 110, // 7
//            400 to 130, // 8
//            350 to 150, // 9
//            320 to 175, // 10
//            300 to 200, // 11
//            280 to 230, // 12
//            260 to 260, // 13
//            240 to 295, // 14
//            220 to 330, // 15
//            200 to 370, // 16
//            180 to 410, // 17
//            160 to 455, // 18
//            140 to 505, // 19
//            120 to 555, // 20
//        )

        val levels = arrayOf(
            Level(800L, 0),   // 0
            Level(750L, 1),   // 1
            Level(700L, 2),   // 2
            Level(650L, 3),   // 3
            Level(600L, 4),   // 4
            Level(550L, 5),   // 5
            Level(500L, 6),   // 6
            Level(450L, 7),  // 7
            Level(400L, 8),  // 8
            Level(350L, 9),  // 9
            Level(320L, 10),  // 10
            Level(300L, 11),  // 11
            Level(280L, 12),  // 12
            Level(260L, 13),  // 13
            Level(240L, 14),  // 14
            Level(220L, 15),  // 15
            Level(200L, 16),  // 16
            Level(180L, 17),  // 17
            Level(160L, 18),  // 18
            Level(140L, 19),  // 19
            Level(120L, 20),  // 20
        )

        data class Level(val speed: Long, val lines: Int)
    }
}