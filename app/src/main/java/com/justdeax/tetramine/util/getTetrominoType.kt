package com.justdeax.tetramine.util

fun getTetrominoType(shape: Array<IntArray>): Int {
    for (array in shape)
        for (number in array)
            if (number != 0) return number
    return 0
}