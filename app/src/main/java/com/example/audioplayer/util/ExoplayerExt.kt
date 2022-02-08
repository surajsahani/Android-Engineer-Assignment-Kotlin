package com.example.audioplayer.util

import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.PlaybackParameters

var ExoPlayer.playbackSpeed: Float
    get() = playbackParameters.speed
    set(speed) {
        val pitch = playbackParameters.pitch
        playbackParameters = PlaybackParameters(speed, pitch)
    }