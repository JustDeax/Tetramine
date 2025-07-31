package com.justdeax.tetramine.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import com.justdeax.tetramine.game.GameController
import kotlin.math.abs

class BoardView(context: Context, attrs: AttributeSet? = null) : BaseBoardView(context, attrs) {
    override var rows = 20
    override var cols = 10
    override var board = Array(rows) { IntArray(cols) }

    private var xSensitivity = 0f
    private var ySensitivity = 0f
    private var is2DirectionRotation = false
    private var maxTimeHDT = 0
    private var minSoftDropsHDT = 0

    private lateinit var game: GameController
    private var touchX = 0f
    private var touchY = 0f
    private var xMotion = 0
    private var yMotion = 0
    private var motionTime = 0L

    var rotateCount = 0
    var hardDropCount = 0

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchX = event.x
                touchY = event.y
                motionTime = System.currentTimeMillis()
            }
            MotionEvent.ACTION_MOVE -> {
                val diffX = event.x - touchX
                val diffY = event.y - touchY
                val thresholdX = width / cols * xSensitivity
                val thresholdY = height / rows * ySensitivity

                if (abs(diffX) > thresholdX && abs(diffY) < thresholdY) {
                    if (diffX > 0) game.moveRight() else game.moveLeft()
                    xMotion++
                    touchY = event.y
                    touchX = event.x
                } else if (diffY > thresholdY) {
                    if (game.currentPiece.row > 0) {
                        game.softDrop()
                        yMotion++
                    }
                    touchY = event.y
                }
            }
            MotionEvent.ACTION_UP -> {
                val diffTime = System.currentTimeMillis() - motionTime
                if (xMotion == 0 && yMotion >= minSoftDropsHDT && diffTime < maxTimeHDT && game.currentPiece.row > rows * 0.15) {
                    game.hardDrop()
                    hardDropCount++
                } else if (xMotion == 0 && yMotion == 0) {
                    performClick()
                    rotateCount++

                    if (is2DirectionRotation) {
                        val pieceCol = game.currentPiece.col
                        val pieceWidth = game.currentPiece.shape[0].size
                        val centerCol =
                            pieceCol + pieceWidth / 2 - if (pieceCol + pieceWidth == cols) 1 else 0

                        val colTouched = touchX / width * cols
                        if (colTouched < centerCol)
                            game.rotateLeft()
                        else game.rotateRight()
                    } else { game.rotateRight() }
                }

                touchX = 0f
                touchY = 0f
                xMotion = 0
                yMotion = 0
            }
        }
        return true
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    fun setControls(
        game: GameController,
        xSensitivity: Float,
        ySensitivity: Float,
        is2DirectionRotation: Boolean,
        maxTimeHDT: Int,
        minSoftDropsHDT: Int
    ) {
        this.game = game
        this.xSensitivity = xSensitivity
        this.ySensitivity = ySensitivity
        this.is2DirectionRotation = is2DirectionRotation
        this.maxTimeHDT = maxTimeHDT
        this.minSoftDropsHDT = minSoftDropsHDT
    }
}