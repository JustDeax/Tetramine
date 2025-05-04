package com.justdeax.tetramine.util

import com.justdeax.tetramine.game.TetramineGameViewModel
import java.util.Locale

fun getStatistics(linesCount: Int, scoreValue: Int): String {
    val lines = String.format("%-6s", String.format(Locale.getDefault(), "%03d", linesCount))
    val score = String.format(Locale.getDefault(), "%06d", scoreValue)
    return "Lines:$lines\nScore:$score"
}

fun getStatistics(linesCount: Int, scoreValue: Int, levelNumber: Int): String {
    val level =
        if (levelNumber == TetramineGameViewModel.levels.lastIndex) " Σ 20 "
        else String.format(Locale.getDefault(), "%02d", levelNumber)
    return getStatistics(linesCount, scoreValue) + "\nLevel:" + String.format("%-6s", level)
}