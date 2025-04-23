package com.justdeax.tetramine.game
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.justdeax.tetramine.util.one
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
    showBanner: (String) -> Unit
): ViewModel() {
    private val tetramine = Tetramine(rows, cols, showBanner)
    private var startedSpeed = 500L
    private var dropSpeed = startedSpeed
    private var gameJob: Job? = null

    val currentPiece get() = tetramine.currentPiece
    val previousPiece get() = tetramine.previousPiece
    val isGameOver get() = tetramine.isGameOver
    val lines get() = tetramine.lines
    val score get() = tetramine.score

    private val _board = MutableStateFlow(tetramine.getBoardWithPiece())
    val board = _board.asStateFlow()

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
                        tetramine.softDrop()
                        _board.value = tetramine.getBoardWithPiece()
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
        dropSpeed = startedSpeed
        _board.value = tetramine.getBoardWithPiece()
    }

    fun moveLeft() {
        gameAction {
            tetramine.moveLeft()
        }
    }

    fun moveRight() {
        gameAction {
            tetramine.moveRight()
        }
    }

    fun rotateLeft() {
        gameAction {
            tetramine.rotateLeft()
        }
    }

    fun rotateRight() {
        gameAction {
            tetramine.rotateRight()
        }
    }

    fun hardDrop() {
        gameAction {
            tetramine.hardDrop()
        }
    }

    fun softDrop() {
        gameAction {
            tetramine.softDrop()
            tetramine.score += one
        }
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
}