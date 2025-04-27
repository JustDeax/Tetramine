package com.justdeax.tetramine.game

import com.justdeax.tetramine.util.AddScore

class Tetramine(
    private val rows: Int,
    private val cols: Int,
    private val showAchievement: (String) -> Unit
) {
    private var board = Array(rows) { IntArray(cols) }
    private var bag = makeBag()
    private var comboCount = -1
    var currentPiece = emptyPiece(); private set
    var previousPiece = nextPiece(); private set
    var isGameOver = false; private set
    var lines = 0; private set
    var score = 0; private set

    init { spawnPiece() }

    fun resetGame() {
        board = Array(rows) { IntArray(cols) }
        bag = makeBag()
        currentPiece = emptyPiece()
        previousPiece = nextPiece()
        isGameOver = false
        lines = 0
        score = 0
        comboCount = -1
        spawnPiece()
    }

    fun getBoardWithPiece(): Array<IntArray> {
        val board = board.map { it.clone() }.toTypedArray()
        applyPieceToBoard(board, ghostPiece())
        applyPieceToBoard(board, currentPiece)
        return board
    }

    fun dropPiece() {
        if (!movePiece(1, 0))
            baseDrop()
    }

    fun softDrop() {
        dropPiece()
        score += AddScore.ONE
    }

    fun hardDrop() {
        val ghost = ghostPiece()
        val distance = ghost.row - currentPiece.row
        score += distance * AddScore.TWO
        currentPiece.row = ghost.row
        baseDrop()
    }

    fun moveLeft() = movePiece(0, -1)
    fun moveRight() = movePiece(0, 1)
    fun rotateLeft() = rotatePiece(isRightRotated = false)
    fun rotateRight() = rotatePiece(isRightRotated = true)

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

    private fun rotatePiece(isRightRotated: Boolean) {
        val kickOffsets = if (isRightRotated) listOf(
            0 to 0,
            0 to 1,
            0 to -1,
            1 to 0,
            1 to 1,
            1 to -1
        ) else listOf(
            0 to 0,
            0 to -1,
            0 to 1,
            1 to 0,
            1 to -1,
            1 to 1
        )

        val rotated = if (isRightRotated)
            currentPiece.rotateRight()
        else
            currentPiece.rotateLeft()
        for ((rowOffset, colOffset) in kickOffsets) {
            val newRow = rotated.row + rowOffset
            val newCol = rotated.col + colOffset
            if (isValidMove(rotated, newRow, newCol)) {
                currentPiece = rotated
                currentPiece.row = newRow
                currentPiece.col = newCol
                return
            }
        }

        val current = currentPiece.copy()
        for ((rowOffset, colOffset) in kickOffsets) {
            val newRow = current.row + rowOffset
            val newCol = current.col + colOffset
            if (isValidMove(current, newRow, newCol)) {
                currentPiece.row = newRow
                currentPiece.col = newCol
                return
            }
        }
    }

    private fun clearLines() {
        val newBoard = board.filter { row -> row.any { it == 0 } }.toTypedArray()
        val cleared = rows - newBoard.size
        board = Array(cleared) { IntArray(cols) } + newBoard

        if (cleared > 0) {
            lines += cleared
            comboCount += 1
            score += comboCount * AddScore.COMBO

            when (comboCount) {
                3 -> showAchievement("COMBO X3")
                5 -> showAchievement("COMBO X5")
                10 -> showAchievement("COMBO X10")
            }
            when (cleared) {
                1 -> score += AddScore.SINGLE
                2 -> score += AddScore.DOUBLE
                3 -> score += AddScore.TRIPLE
                4 -> { score += AddScore.TETRAMINE; showAchievement("TETRAMINE") }
            }
            if (board.last().all { it == 0 }) {
                score += AddScore.PERFECT_CLEAR
                showAchievement("PERFECT CLEAR")
            }
        } else {
            comboCount = -1
        }
    }

    private fun spawnPiece() {
        currentPiece = previousPiece
        previousPiece = nextPiece()
        currentPiece.row = 0
        currentPiece.col = cols / 2 - (currentPiece.shape[0].size / 2)
        if (!isValidMove(currentPiece, currentPiece.row, currentPiece.col))
            isGameOver = true
    }

    private fun ghostPiece(): Tetromino {
        val ghost = currentPiece.copy()
        while (isValidMove(ghost, ghost.row + 1, ghost.col))
            ghost.row++
        return ghost
    }

    private fun emptyPiece() = Tetromino(arrayOf(intArrayOf()))

    private fun nextPiece(): Tetromino {
        if (bag.isEmpty())
            bag = makeBag()
        return Tetromino(bag.removeAt(0))
    }

    private fun makeBag() = Tetromino.TETROMINO_SHAPES.toMutableList().apply { shuffle() }

    private fun isValidMove(piece: Tetromino, newRow: Int, newCol: Int): Boolean {
        return forEachCell(piece) { i, j ->
            val row = newRow + i
            val col = newCol + j
            row in 0 until rows && col in 0 until cols && board[row][col] == 0
        }
    }

    private fun applyPieceToBoard(board: Array<IntArray>, piece: Tetromino) {
        forEachCell(piece) { i, j ->
            val row = piece.row + i
            val col = piece.col + j
            if (row in board.indices && col in board[0].indices)
                board[row][col] = piece.shape[i][j]
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