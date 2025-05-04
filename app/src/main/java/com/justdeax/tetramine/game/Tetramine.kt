package com.justdeax.tetramine.game

import com.justdeax.tetramine.util.constant.AddScore

class Tetramine(
    private val rows: Int,
    private val cols: Int,
    private val showAchievement: (String) -> Unit,
    private val level: () -> Int,
) {
    private var board = Array(rows) { IntArray(cols) }
    private var bag = makeBag()
    private var comboCount = -1
    var currentPiece = emptyPiece()
    var previousPiece = nextPiece()
    var isGameOver = false
    var lines = 0
    var score = 0

    init { spawnPiece() }

    fun resetGame() {
        board = Array(rows) { IntArray(cols) }
        bag = makeBag()
        comboCount = -1
        currentPiece = emptyPiece()
        previousPiece = nextPiece()
        isGameOver = false
        lines = 0
        score = 0
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
            0 to 0, 0 to 1, 0 to -1, 1 to 0, 1 to 1, 1 to -1
        ) else listOf(
            0 to 0, 0 to -1, 0 to 1, 1 to 0, 1 to -1, 1 to 1
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

        for ((rowOffset, colOffset) in kickOffsets) {
            val newRow = currentPiece.row + rowOffset
            val newCol = currentPiece.col + colOffset
            if (isValidMove(currentPiece, newRow, newCol)) {
                currentPiece.row = newRow
                currentPiece.col = newCol
                return
            }
        }
    }

    private fun clearLines() {
        val newBoard = board.filter { row -> row.any { it == 0 } }.toTypedArray()
        val cleared = rows - newBoard.size
        if (cleared == 0) {
            comboCount = -1
            return
        }
        board = Array(cleared) { IntArray(cols) } + newBoard
        lines += cleared
        comboCount++

        val isPerfectClear = board.last().all { it == 0 }
        score += comboCount * AddScore.COMBO * level()
        score += if (isPerfectClear) AddScore.PERFECT_CLEAR * level() else 0
        score += when (cleared) {
            1 -> AddScore.SINGLE * level()
            2 -> AddScore.DOUBLE * level()
            3 -> AddScore.TRIPLE * level()
            4 -> AddScore.TETRAMINE * level()
            else -> 0
        }

        showAchievement(
            buildString {
                if (isPerfectClear) appendLine("PERFECT_CLEAR")
                if (cleared == 4) appendLine("TETRAMINE")
                when (comboCount) {
                    3 -> appendLine("COMBO X3")
                    5 -> appendLine("COMBO X5")
                    7 -> appendLine("COMBO X7")
                }
            }
        )
    }

    private fun spawnPiece() {
        currentPiece = previousPiece
        currentPiece.row = 0
        currentPiece.col = cols / 2 - (currentPiece.shape[0].size / 2)
        previousPiece = nextPiece()
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