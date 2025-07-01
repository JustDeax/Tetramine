package com.justdeax.tetramine.game

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.justdeax.tetramine.PreferenceManager.music
import kotlinx.coroutines.*

class MusicManager(context: Context, private val crossfadeDuration: Long) {
    private val appContext = context.applicationContext

    private var currentPlayer: ExoPlayer? = null
    private var nextPlayer: ExoPlayer? = null

    private var currentResId: Int = 0
    private val maxVolume = 0.7f

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var loopJob: Job? = null

    var enabled = appContext.music

    fun toggle(): Boolean {
        appContext.music = !enabled
        enabled = appContext.music

        return enabled
    }

    fun play(resId: Int = 0) {
        if (!enabled) return
        when {
            currentPlayer == null -> start(resId)
            currentPlayer?.isPlaying == false -> resume()
            currentResId != resId && resId != 0 -> beginCrossfade(resId)
        }
    }

    private fun start(resId: Int) {
        currentResId = resId
        currentPlayer?.release()
        currentPlayer = ExoPlayer.Builder(appContext).build().apply {
            val mediaItem = MediaItem.fromUri("android.resource://${appContext.packageName}/$resId")
            setMediaItem(mediaItem)
            prepare()
            volume = maxVolume
            play()
        }

        loopJob?.cancel()
        loopJob = scope.launch {
            delayUntilCrossfade(currentPlayer)
            beginCrossfade(currentResId)
        }
    }

    private fun resume() {
        currentPlayer?.play()
        nextPlayer?.play()
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
                currentPlayer?.volume = maxVolume * (1f - progress)
                nextPlayer?.volume = maxVolume * progress
                delay(delayStep)
            }

            currentPlayer?.release()
            currentPlayer = nextPlayer
            nextPlayer = null
            currentResId = newResId

            delayUntilCrossfade(currentPlayer)
            beginCrossfade(currentResId)
        }
    }

    private suspend fun delayUntilCrossfade(player: ExoPlayer?) {
        player?.let {
            while (!it.isPlaying || it.duration <= 0)
                delay(100)
            delay((it.duration - crossfadeDuration).coerceAtLeast(0))
        }
    }

    fun pause() {
        currentPlayer?.pause()
        nextPlayer?.pause()
    }

    fun release() {
        loopJob?.cancel()
        scope.coroutineContext.cancelChildren()
        currentPlayer?.release()
        nextPlayer?.release()
        currentPlayer = null
        nextPlayer = null
        currentResId = 0
    }
}