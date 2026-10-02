package com.example.notification

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import com.example.R

object SoundPreviewHelper {

    private var mediaPlayer: MediaPlayer? = null

    fun playDragonChime(context: Context, onCompletion: (() -> Unit)? = null) {
        try {
            stop()
            mediaPlayer = MediaPlayer.create(context.applicationContext, R.raw.dragon_chime)?.apply {
                setOnCompletionListener {
                    stop()
                    onCompletion?.invoke()
                }
                start()
            }
        } catch (e: Exception) {
            Log.e("SoundPreviewHelper", "Failed to play dragon chime", e)
            onCompletion?.invoke()
        }
    }

    fun stop() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.release()
            }
        } catch (e: Exception) {
            Log.e("SoundPreviewHelper", "Error releasing MediaPlayer", e)
        } finally {
            mediaPlayer = null
        }
    }
}
