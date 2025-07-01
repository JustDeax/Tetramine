package com.justdeax.tetramine.game

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class TetramineGameFactory(
    private val application: Application,
    private val rows: Int,
    private val cols: Int,
    private val showAchievement: (String) -> Unit
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        if (modelClass.isAssignableFrom(TetramineGameViewModel::class.java))
            return TetramineGameViewModel(application, rows, cols, showAchievement) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}