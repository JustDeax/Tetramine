package com.justdeax.tetramine.game

interface GameController {
    fun moveLeft()
    fun moveRight()
    fun softDrop()
    fun hardDrop()
    fun rotateLeft()
    fun rotateRight()
    val currentPiece: Tetromino
}