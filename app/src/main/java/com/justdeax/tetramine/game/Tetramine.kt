package com.justdeax.tetramine.game

import com.justdeax.tetramine.util.constant.AddScore
import com.justdeax.tetramine.util.constant.Text
import com.justdeax.tetramine.util.getTetrominoType

class Tetramine(
    private val rows: Int,
    private val cols: Int,
    private val showAchievement: (String) -> Unit,
    isShowGhostPiece: Boolean,
    private val getLevel: () -> Int,
) {
    private var board = Array(rows) { IntArray(cols) }
    private var bag = makeBag()
    private var comboCount = -1
    private var lastMoveRotation = false
    private var isBackToBack = false
    var currentPiece = emptyPiece()
    var previousPiece = nextPiece()
    var isGameOver = false
    var score = 0
    var lines = 0
    var pieces = 0
    var fourLines = 0
    var tSpins = 0
    val getBoardWithPiece =
        if (isShowGhostPiece) { -> buildBoard(ghostPiece()) } else { -> buildBoard() }

    init { spawnPiece() }

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

    private fun buildBoard(vararg pieces: Tetromino): Array<IntArray> {
        val boardCopy = board.map { it.clone() }.toTypedArray()
        for (piece in pieces)
            applyPieceToBoard(boardCopy, piece)
        applyPieceToBoard(boardCopy, currentPiece)
        return boardCopy
    }

    private fun baseDrop() {
        applyPieceToBoard(board, currentPiece)
        clearLines(isTSpin(currentPiece))
        spawnPiece()
    }

    private fun movePiece(dRow: Int, dCol: Int): Boolean {
        val newRow = currentPiece.row + dRow
        val newCol = currentPiece.col + dCol
        if (isValidMove(currentPiece, newRow, newCol)) {
            currentPiece.row = newRow
            currentPiece.col = newCol
            lastMoveRotation = false
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
            1 to -1,
            2 to 0,
            2 to 1,
            2 to -1
        ) else listOf(
            0 to 0,
            0 to -1,
            0 to 1,
            1 to 0,
            1 to -1,
            1 to 1,
            2 to 0,
            2 to -1,
            2 to 1
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
                lastMoveRotation = true
                return
            }
        }
    }

    private fun clearLines(isTSpin: Boolean) {
        val newBoard = board.filter { row -> row.any { it == 0 } }.toTypedArray()
        val cleared = rows - newBoard.size
        if (cleared == 0) {
            comboCount = -1
            isBackToBack = false
            return
        }
        board = Array(cleared) { IntArray(cols) } + newBoard
        lines += cleared
        comboCount += 1

        val basePoints = when {
            isTSpin && cleared == 1 -> { AddScore.T_SPIN_SINGLE; tSpins++ }
            isTSpin && cleared == 2 -> { AddScore.T_SPIN_DOUBLE; tSpins++ }
            cleared == 1 -> { AddScore.SINGLE }
            cleared == 2 -> { AddScore.DOUBLE }
            cleared == 3 -> { AddScore.TRIPLE }
            cleared == 4 -> { AddScore.TETRAMINE; fourLines++ }
            else -> 0
        }

        val isPerfectClear = board.last().all { it == 0 }
        val extraPoints = comboCount * AddScore.COMBO + if (isPerfectClear) AddScore.PERFECT_CLEAR else 0

        val isNewBackToBack = isTSpin || cleared == 4
        val b2bPoints = if (isNewBackToBack && isBackToBack) basePoints / 2 else 0
        isBackToBack = isNewBackToBack

        score += (basePoints + extraPoints + b2bPoints) * getLevel()

        if (getLevel() <= 10)
            showAchievement(
                buildString {
                    if (b2bPoints > 0) appendLine(Text.B2B)
                    else if (cleared == 4) appendLine(Text.TETRAMINE)
                    else if (isTSpin && cleared == 2) appendLine(Text.T_SPIN_DOUBLE)

                    if (isPerfectClear) appendLine(Text.PERFECT_CLEAR)
                    else when (comboCount) {
                        3 -> appendLine(Text.COMBO_X + 3)
                        5 -> appendLine(Text.COMBO_X + 5)
                        7 -> appendLine(Text.COMBO_X + 7)
                    }
                }
            )
    }

    private fun isTSpin(piece: Tetromino): Boolean {
        if (!lastMoveRotation || getTetrominoType(piece.shape) != 3)
            return false

        val row = piece.row + 1
        val col = piece.col + 1
        val corners = listOf(
            row - 1 to col - 1,
            row - 1 to col + 1,
            row + 1 to col - 1,
            row + 1 to col + 1
        )
        val blocked = corners.count { (r, c) ->
            r !in 0 until rows || c !in 0 until cols || board[r][c] != 0
        }

        return blocked == 3
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
        if (bag.isEmpty()) {
            bag = makeBag()
            pieces += Tetromino.TETROMINO_SHAPES.size
        }
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