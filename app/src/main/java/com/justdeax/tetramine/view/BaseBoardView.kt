package com.justdeax.tetramine.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.justdeax.tetramine.util.dpToPx

abstract class BaseBoardView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {
    protected abstract var rows: Int
    protected abstract var cols: Int
    protected abstract var board: Array<IntArray>

    private var colors = intArrayOf()
    private var cellSpacing = 0f
    private var cellCornerRadius = 0f

    private val rect = RectF()
    private val paint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        val ratio = rows.toFloat() / cols.toFloat()
        val finalWidth: Int
        val finalHeight: Int

        if ((width * ratio) > height) {
            finalHeight = height
            finalWidth = (height / ratio).toInt()
        } else {
            finalWidth = width
            finalHeight = (width * ratio).toInt()
        }
        setMeasuredDimension(finalWidth, finalHeight)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val cellSize = (width / cols.toFloat()) - cellSpacing
        for (row in 0 until rows) {
            for (col in 0 until cols) {
                val value = board[row][col]
                if (value in colors.indices) {
                    val left = col * (cellSize + cellSpacing)
                    val top = row * (cellSize + cellSpacing)
                    val right = left + cellSize
                    val bottom = top + cellSize

                    paint.color = colors[value]
                    rect.set(left, top, right, bottom)
                    canvas.drawRoundRect(rect, cellCornerRadius, cellCornerRadius, paint)
                }
            }
        }
    }

    open fun update(newBoard: Array<IntArray>) {
        board = newBoard
        invalidate()
    }

    fun setStyle(colors: IntArray, cellSpacing: Float, cellCornerRadius: Float) {
        this.colors = colors
        this.cellSpacing = context.dpToPx(cellSpacing)
        this.cellCornerRadius = context.dpToPx(cellCornerRadius)
        invalidate()
    }
}