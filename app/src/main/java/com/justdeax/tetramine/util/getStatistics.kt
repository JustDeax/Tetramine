package com.justdeax.tetramine.util

import java.util.Locale

fun getStatistics(linesCount: Int, scoreValue: Int): String {
    val lines = String.format("%-6s", String.format(Locale.getDefault(), "%03d", linesCount))
    val score = String.format(Locale.getDefault(), "%06d", scoreValue)
    return "Lines:$lines\nScore:$score"
}