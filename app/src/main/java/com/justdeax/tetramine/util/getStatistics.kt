package com.justdeax.tetramine.util

import com.justdeax.tetramine.game.TetramineGameViewModel
import com.justdeax.tetramine.util.constant.Text

fun getStatistics(linesCount: Int, scoreValue: Int): String {
    val linesStr = linesCount.toString().padEnd(3, '0')
    val scoreStr = scoreValue.toString().padEnd(maxOf(6, scoreValue.toString().length), '0')

    val linesPadded = linesStr.padEnd(scoreStr.length, ' ')
    val scorePadded = scoreStr.padEnd(scoreStr.length, ' ')

    return "${Text.LINES}$linesPadded\n${Text.SCORE}$scorePadded"
}

fun getStatistics(linesCount: Int, scoreValue: Int, levelNumber: Int): String {
    val baseStats = getStatistics(linesCount, scoreValue)

    val levelText = if (levelNumber == TetramineGameViewModel.levels.lastIndex)
        Text.SIGMA_20
    else
        levelNumber.toString().padEnd(2, '0')
    val levelPadded = levelText.padEnd(maxOf(6, scoreValue.toString().length), ' ')

    return "$baseStats\n${Text.LEVEL}$levelPadded"
}