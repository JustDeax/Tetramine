package com.justdeax.tetramine.util

fun getTetrominoType(board: Array<IntArray>): Int {
    for (array in board)
        for (number in array)
            if (number != 0) return number
    return 0
}