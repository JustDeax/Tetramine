package com.justdeax.tetramine.game

class Tetromino(val shape: Array<IntArray>, var row: Int = 0, var col: Int = 0) {

    fun copy() = Tetromino(
        shape.map { row ->
            row.map { cell ->
                if (cell == 0) 0 else 8
            }.toIntArray()
        }.toTypedArray(),
        row, col
    )

    fun rotateRight() = Tetromino(rotateMatrix(clockwise = true), row, col)

    fun rotateLeft() = Tetromino(rotateMatrix(clockwise = false), row, col)

    private fun rotateMatrix(clockwise: Boolean): Array<IntArray> {
        val rows = shape.size
        val cols = shape[0].size
        val rotated = Array(cols) { IntArray(rows) }

        if (clockwise)
            for (i in shape.indices)
                for (j in shape[i].indices)
                    rotated[j][rows - i - 1] = shape[i][j]
        else
            for (i in shape.indices)
                for (j in shape[i].indices)
                    rotated[cols - j - 1][i] = shape[i][j]
        return rotated
    }

    companion object {
        val TETROMINO_SHAPES = listOf(
            arrayOf(
                // I
                intArrayOf(0, 0, 0, 0),
                intArrayOf(1, 1, 1, 1),
                intArrayOf(0, 0, 0, 0),
            ),
            arrayOf(
                // O
                intArrayOf(2, 2),
                intArrayOf(2, 2),
            ),
            arrayOf(
                // T
                intArrayOf(0, 3, 0),
                intArrayOf(3, 3, 3),
                intArrayOf(0, 0, 0),
            ),
            arrayOf(
                // L
                intArrayOf(4, 0, 0),
                intArrayOf(4, 4, 4),
                intArrayOf(0, 0, 0),
            ),
            arrayOf(
                // J
                intArrayOf(0, 0, 5),
                intArrayOf(5, 5, 5),
                intArrayOf(0, 0, 0),
            ),
            arrayOf(
                // S
                intArrayOf(0, 6, 6),
                intArrayOf(6, 6, 0),
                intArrayOf(0, 0, 0),
            ),
            arrayOf(
                // Z
                intArrayOf(7, 7, 0),
                intArrayOf(0, 7, 7),
                intArrayOf(0, 0, 0),
            )
        )
    }
}