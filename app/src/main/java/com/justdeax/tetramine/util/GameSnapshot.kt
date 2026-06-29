package com.justdeax.tetramine.util

import com.justdeax.tetramine.game.Tetromino

data class GameSnapshot(
    val board: Array<IntArray>,
    val bag: MutableList<Array<IntArray>>,
    val comboCount: Int,
    val lastMoveRotation: Boolean,
    val isBackToBack: Boolean,
    val currentPiece: Tetromino,
    val previousPiece: Tetromino,
    val isGameOver: Boolean,
    val score: Int,
    val lines: Int,
    val pieces: Int,
    val fourLines: Int,
    val tSpins: Int
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GameSnapshot

        if (comboCount != other.comboCount) return false
        if (lastMoveRotation != other.lastMoveRotation) return false
        if (isBackToBack != other.isBackToBack) return false
        if (isGameOver != other.isGameOver) return false
        if (score != other.score) return false
        if (lines != other.lines) return false
        if (pieces != other.pieces) return false
        if (fourLines != other.fourLines) return false
        if (tSpins != other.tSpins) return false
        if (!board.contentDeepEquals(other.board)) return false
        if (bag != other.bag) return false
        if (currentPiece != other.currentPiece) return false
        if (previousPiece != other.previousPiece) return false

        return true
    }

    override fun hashCode(): Int {
        var result = comboCount
        result = 31 * result + lastMoveRotation.hashCode()
        result = 31 * result + isBackToBack.hashCode()
        result = 31 * result + isGameOver.hashCode()
        result = 31 * result + score
        result = 31 * result + lines
        result = 31 * result + pieces
        result = 31 * result + fourLines
        result = 31 * result + tSpins
        result = 31 * result + board.contentDeepHashCode()
        result = 31 * result + bag.hashCode()
        result = 31 * result + currentPiece.hashCode()
        result = 31 * result + previousPiece.hashCode()
        return result
    }
}
