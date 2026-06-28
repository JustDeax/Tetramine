package com.justdeax.tetramine.game

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.justdeax.tetramine.PreferenceManager.isMusicEnable
import com.justdeax.tetramine.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class MusicManager(context: Context, private val crossfadeDuration: Long) {
    private val appContext = context.applicationContext

    private var currentPlayer: ExoPlayer? = null
    private var nextPlayer: ExoPlayer? = null

    private var currentResId: Int = 0
    private val maxVolume = 0.5f

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var loopJob: Job? = null

    fun play(level: Int) {
        if (!appContext.isMusicEnable) return

        val resId = when (level) {
            in 0..9 -> R.raw.tetris_remix
            in 10..19 -> R.raw.tetris_remix_t_spin
            20 -> R.raw.tetris_death_mode
            else -> 0
        }

        when {
            currentPlayer == null -> start(resId)
            currentResId == resId && currentPlayer?.isPlaying == true -> return
            currentResId != resId -> beginCrossfade(resId)
            else -> resume()
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
                delay(delayStep.milliseconds)
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
                delay(100.milliseconds)

            val crossfadeStart = it.duration - crossfadeDuration
            var remaining = crossfadeStart.coerceAtLeast(0)
            val step = 200L

            while (remaining > 0) {
                delay(step.milliseconds)
                if (it.isPlaying)
                    remaining -= step
            }
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