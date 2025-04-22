package com.justdeax.tetramine.game.view
import android.content.Context
import android.util.AttributeSet
import com.justdeax.tetramine.util.tetrominoType
import com.justdeax.tetramine.util.padArray2x4

class PreviewView(context: Context, attrs: AttributeSet? = null) : BaseBoardView(context, attrs) {
    override var rows = 2
    override var cols = 4
    override var board = Array(rows) { IntArray(cols) }

    override fun updateBoard(newBoard: Array<IntArray>) {
        val padded = padArray2x4(newBoard)
        if (tetrominoType(board) != tetrominoType(padded))
            super.updateBoard(padded)
    }
}