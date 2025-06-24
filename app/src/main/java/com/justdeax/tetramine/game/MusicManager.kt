package com.justdeax.tetramine.game

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.*

class MusicManager(context: Context, private val crossfadeDuration: Long) {
    private val appContext = context.applicationContext

    private var currentPlayer: ExoPlayer? = null
    private var nextPlayer: ExoPlayer? = null

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
        currentPlayer = ExoPlayer.Builder(appContext).build().apply {
            val mediaItem = MediaItem.fromUri("android.resource://${appContext.packageName}/$resId")
            setMediaItem(mediaItem)
            prepare()
            play()
        }
        loopJob?.cancel()
        loopJob = scope.launch {
            delayUntilEnd(currentPlayer)
            if (!isManualSwitch)
                beginCrossfade(currentResId)
        }
    }

    private suspend fun delayUntilEnd(player: ExoPlayer?) {
        val durationMs = player?.duration?.takeIf { it > 0 } ?: 0
        delay((durationMs - crossfadeDuration).coerceAtLeast(0))
    }

    private fun beginCrossfade(newResId: Int) {
        loopJob?.cancel()
        nextPlayer?.release()
        nextPlayer = ExoPlayer.Builder(appContext).build().apply {
            val mediaItem = MediaItem.fromUri("android.resource://${appContext.packageName}/$newResId")
            setMediaItem(mediaItem)
            prepare()
            volume = 0f
            play()
        }
        scope.launch {
            val steps = 30
            val delayStep = crossfadeDuration / steps

            for (i in 0..steps) {
                val progress = i.toFloat() / steps
                currentPlayer?.volume = 1f - progress
                nextPlayer?.volume = progress
                delay(delayStep)
            }
            currentPlayer?.release()
            currentPlayer = nextPlayer
            nextPlayer = null
            isManualSwitch = false

            delayUntilEnd(currentPlayer)
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
        currentPlayer?.play()
        nextPlayer?.play()
    }
}