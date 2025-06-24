package com.justdeax.tetramine.game

import android.content.Context
import android.media.MediaPlayer
import kotlinx.coroutines.*

class MusicManager(context: Context, private val crossfadeDuration: Long) {
    private val appContext = context.applicationContext

    private var currentPlayer: MediaPlayer? = null
    private var nextPlayer: MediaPlayer? = null

    private var currentResId = 0
    private var isManualSwitch = false

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var loopJob: Job? = null

    fun startLoop(resId: Int) {
        currentResId = resId
        isManualSwitch = false
        start(resId)
    }

    private fun start(resId: Int) {
        currentPlayer?.release()
        currentPlayer = MediaPlayer.create(appContext, resId).apply {
            isLooping = false
            setVolume(1f, 1f)
            setOnCompletionListener {
                if (!isManualSwitch)
                    startLoop(currentResId)
            }
            start()
        }

        loopJob?.cancel()
        loopJob = scope.launch {
            val duration = currentPlayer?.duration ?: return@launch
            delay((duration - crossfadeDuration).coerceAtLeast(0))
            if (!isManualSwitch)
                beginCrossfade(currentResId)
        }
    }

    private fun beginCrossfade(newResId: Int) {
        loopJob?.cancel()
        nextPlayer?.release()
        nextPlayer = MediaPlayer.create(appContext, newResId).apply {
            isLooping = false
            setVolume(0f, 0f)
            start()
        }

        scope.launch {
            val steps = 30
            val delayStep = crossfadeDuration / steps

            for (i in 0..steps) {
                val progress = i.toFloat() / steps
                val volumeOut = 1f - progress
                val volumeIn = progress - 0f
                currentPlayer?.setVolume(volumeOut, volumeOut)
                nextPlayer?.setVolume(volumeIn, volumeIn)
                delay(delayStep)
            }

            currentPlayer?.release()
            currentPlayer = nextPlayer
            nextPlayer = null
            isManualSwitch = false

            val duration = currentPlayer?.duration ?: return@launch
            delay((duration - crossfadeDuration).coerceAtLeast(0))
            if (!isManualSwitch)
                beginCrossfade(currentResId)
        }
    }

    fun switchTo(newResId: Int) {
        isManualSwitch = true
        loopJob?.cancel()
        beginCrossfade(newResId)
    }

    fun stop() {
        loopJob?.cancel()
        scope.coroutineContext.cancelChildren()
        currentPlayer?.release()
        nextPlayer?.release()
        currentPlayer = null
        nextPlayer = null
    }

    fun pause() {
        currentPlayer?.pause()
        nextPlayer?.pause()
    }

    fun resume() {
        currentPlayer?.start()
        nextPlayer?.start()
    }
}