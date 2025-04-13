package com.justdeax.tetramine.game

class Tetramine(
    private val rows: Int,
    private val cols: Int,
    private val message: (String) -> Unit
) {
    private var board = Array(rows) { IntArray(cols) }
    var currentPiece = Tetromino.emptyPiece() ; private set
    var previousPiece = Tetromino.randomPiece() ; private set
    var isGameOver = false ; private set
    var lines = 0 ; private set
    var score = 0
    var comboCount = -1

    init { spawnPiece() }

    fun resetGame() {
        board = Array(rows) { IntArray(cols) }
        currentPiece = Tetromino.emptyPiece()
        previousPiece = Tetromino.randomPiece()
        isGameOver = false
        lines = 0
        score = 0
        comboCount = -1
        spawnPiece()
    }

    fun getBoardWithPiece(): Array<IntArray> {
        val board = board.map { it.clone() }.toTypedArray()
        applyPieceToBoard(board, ghostPiece(), true)
        applyPieceToBoard(board, currentPiece)
        return board
    }

    fun softDrop() {
        if (!movePiece(1, 0))
            baseDrop()
    }

    fun hardDrop() {
        val ghost = ghostPiece()
        val distance = ghost.row - currentPiece.row
        currentPiece.row = ghost.row
        score += distance * com.justdeax.tetramine.util.two
        baseDrop()
    }

    fun moveLeft() = movePiece(0, -1)
    fun moveRight() = movePiece(0, 1)
    fun rotateLeft() = rotatePiece(currentPiece.rotateLeft())
    fun rotateRight() = rotatePiece(currentPiece.rotateRight())

    private fun baseDrop() {
        applyPieceToBoard(board, currentPiece)
        clearLines()
        spawnPiece()
    }

    private fun movePiece(dRow: Int, dCol: Int): Boolean {
        val newRow = currentPiece.row + dRow
        val newCol = currentPiece.col + dCol
        if (isValidMove(currentPiece, newRow, newCol)) {
            currentPiece.row = newRow
            currentPiece.col = newCol
            return true
        }
        return false
    }

    private fun rotatePiece(rotated: Tetromino) {
        if (isValidMove(rotated, currentPiece.row, currentPiece.col)) {
            currentPiece = rotated
        } else {
            listOf(1,-1).firstOrNull {
                isValidMove(rotated, currentPiece.row, currentPiece.col + it)
            }?.let {
                currentPiece = rotated
                currentPiece.col += it
            }
        }
    }

    private fun isValidMove(piece: Tetromino, newRow: Int, newCol: Int): Boolean {
        return forEachCell(piece) { i, j ->
            val row = newRow + i
            val col = newCol + j
            row in 0 until rows && col in 0 until cols && board[row][col] == 0
        }
    }

    private fun clearLines() {
        val newBoard = board.filter { row -> row.any { it == 0 } }.toTypedArray()
        val cleared = rows - newBoard.size
        board = Array(cleared) { IntArray(cols) } + newBoard

        if (cleared > 0) {
            lines += cleared
            comboCount++
            score += comboCount * com.justdeax.tetramine.util.combo
            when (comboCount) {
                3 -> message("COMBO X3")
                5 -> message("COMBO X5")
                10 -> message("COMBO X10")
            }
            when (cleared) {
                1 -> score += com.justdeax.tetramine.util.single
                2 -> score += com.justdeax.tetramine.util.double
                3 -> score += com.justdeax.tetramine.util.triple
                4 -> { score += com.justdeax.tetramine.util.tetramine
                       message("TETRAMINE") }
            }
            if (board.all { row -> row.all { it == 0 } }) {
                score += com.justdeax.tetramine.util.perfectClear
                message("PERFECT CLEAR")
            }
        } else {
            comboCount = -1
        }
    }

    private fun spawnPiece() {
        currentPiece = previousPiece
        previousPiece = Tetromino.randomPiece()
        currentPiece.row = 0
        currentPiece.col = cols / 2 - (currentPiece.shape[0].size / 2)
        if (!isValidMove(currentPiece, currentPiece.row, currentPiece.col))
            isGameOver = true
    }

    private fun ghostPiece(): Tetromino {
        val ghost = currentPiece.copy()
        while (isValidMove(ghost, ghost.row + 1, ghost.col))
            ghost.row += 1
        return ghost
    }

    private fun applyPieceToBoard(board: Array<IntArray>, piece: Tetromino, isGhost: Boolean = false) {
        forEachCell(piece) { i, j ->
            val row = piece.row + i
            val col = piece.col + j
            if (row in board.indices && col in board[0].indices)
                board[row][col] = if (isGhost) 8 else piece.shape[i][j]
            true
        }
    }

    private inline fun forEachCell(piece: Tetromino, action: (Int, Int) -> Boolean): Boolean {
        for (i in piece.shape.indices)
            for (j in piece.shape[i].indices)
                if (piece.shape[i][j] != 0 && !action(i, j)) return false
        return true
    }
}