package com.justdeax.tetramine.view

import android.content.Context
import android.util.AttributeSet

class BoardView(context: Context, attrs: AttributeSet? = null) : BaseBoardView(context, attrs) {
    override var rows = 20
    override var cols = 10
    override var board = Array(rows) { IntArray(cols) }
}