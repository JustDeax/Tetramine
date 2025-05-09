package com.justdeax.tetramine.util

import com.justdeax.tetramine.game.TetramineGameViewModel
import com.justdeax.tetramine.util.constant.Text
import java.util.Locale

fun getStatistics(linesCount: Int, scoreValue: Int): String {
    val lines = String.format("%-6s", String.format(Locale.getDefault(), "%03d", linesCount))
    val score = String.format(Locale.getDefault(), "%06d", scoreValue)
    return Text.LINES + lines + "\n" + Text.SCORE + score
}

fun getStatistics(linesCount: Int, scoreValue: Int, levelNumber: Int): String {
    val level = String.format("%-6s",
        if (levelNumber == TetramineGameViewModel.levels.lastIndex) Text.SIGMA_20
        else String.format(Locale.getDefault(), "%02d", levelNumber)
    )
    return getStatistics(linesCount, scoreValue) + "\n" + Text.LEVEL + level
}