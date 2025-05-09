package com.justdeax.tetramine.view

import android.content.Context
import android.util.AttributeSet
import com.justdeax.tetramine.util.getTetrominoType

class PreviewView(context: Context, attrs: AttributeSet? = null) : BaseBoardView(context, attrs) {
    override var rows = 2
    override var cols = 4
    override var board = Array(rows) { IntArray(cols) }

    override fun update(newBoard: Array<IntArray>) {
        if (getTetrominoType(board) == getTetrominoType(newBoard)) return

        val padded = Array(rows) { rowIndex ->
            val row = newBoard.getOrNull(rowIndex) ?: intArrayOf()
            val paddedRow = IntArray(cols)
            for (i in row.indices)
                if (i < cols) paddedRow[cols - row.size + i] = row[i]
            paddedRow
        }
        super.update(padded)
    }
}