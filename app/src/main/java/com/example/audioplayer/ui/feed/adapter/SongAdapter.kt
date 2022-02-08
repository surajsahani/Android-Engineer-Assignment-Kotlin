package com.example.audioplayer.ui.feed.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.audioplayer.R
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.example.audioplayer.data.Song
import com.example.audioplayer.databinding.ItemSongPlayerBinding
import com.example.audioplayer.util.playbackSpeed


class SongAdapter(
    private val context: Context,
    private val onEvent: (view: View?, song: Song) -> Unit
) :
    RecyclerView.Adapter<SongAdapter.SongViewHolder?>() {
    companion object {
        private const val TAG = "SongAdapter"
    }

    var data = ArrayList<Song>()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var player: ExoPlayer? = null
    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition = 0L

    init {
        initializePlayer()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        return SongViewHolder(
            ItemSongPlayerBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class SongViewHolder(val binding: ItemSongPlayerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        lateinit var durationRunnable: Runnable

        fun bind(song: Song) {
            binding.apply {
                textViewSongTitle.text = song.title
                textViewArtistName.text = song.creator.email
                updatePlaybackText()
            }

            binding.buttonShare.setOnClickListener {
                onEvent(it, song)
            }

            binding.buttonDownload.setOnClickListener {
                onEvent(it, song)
            }

            binding.buttonVolume.setOnClickListener {
                val currentVolume = player?.volume
                if (currentVolume == 0f) {
                    player?.volume = 1f
                } else {
                    player?.volume = 0f
                }
                onEvent(it, song)
            }

            binding.buttonFavorite.setOnCheckedChangeListener { button, isChecked ->
                song.isFavorite = isChecked
                onEvent(button, song)
            }

            binding.textViewPlayBackSpeed.setOnClickListener {
                val newSpeed = when (player?.playbackSpeed) {
                    1f -> 2f
                    2f -> 3f
                    else -> 1f
                }
                player?.playbackSpeed = newSpeed
                updatePlaybackText()
            }

            playSong(song.audioUrl)

            setupPlayPauseButton()

            updatePlayButtonUi(playWhenReady)

            player?.addListener(object : Player.Listener {
                override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                    super.onPlayWhenReadyChanged(playWhenReady, reason)
                    updatePlayButtonUi(playWhenReady)
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    super.onPlaybackStateChanged(playbackState)
                    if (playbackState == Player.STATE_ENDED) {
                        if (data.indexOf(song) != data.lastIndex &&
                            ((player!!.currentPosition * 100) / player!!.duration)
                                .toInt() == 100
                        ) {
                            onEvent(binding.progressBar, song)
                        }
                    }
                }
            })

            setupProgressIndicator()
        }

        private fun updatePlaybackText() {
            val playBackText = when (player?.playbackSpeed) {
                1f -> R.string.label_1x
                2f -> R.string.label_2x
                else -> R.string.label_3x
            }

            binding.textViewPlayBackSpeed.text =
                binding.textViewPlayBackSpeed.context.getText(playBackText)
        }

        private fun playSong(songUrl: String) {
            Log.d(TAG, "Playing Song: $songUrl")
            player?.clearMediaItems()
            val mediaItem =
                MediaItem.fromUri(songUrl)
            player?.setMediaItem(mediaItem)
            player?.play()
        }

        private fun setupPlayPauseButton() {
            binding.imageViewPlayPause.setOnClickListener {
                if (player?.isPlaying == true) {
                    player?.pause()
                } else {
                    player?.play()
                }
            }
        }

        private fun setupProgressIndicator() {
            val handler = Handler(Looper.getMainLooper())
            durationRunnable = Runnable {
                player?.let {
                    binding.progressBar.progress =
                        ((player!!.currentPosition * 100) / player!!.duration).toInt()
                    handler.postDelayed(durationRunnable, 1)
                }
            }
            handler.postDelayed(durationRunnable, 0)
        }

        private fun updatePlayButtonUi(playWhenReady: Boolean) {
            if (playWhenReady) {
                binding.imageViewPlayPause.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        context.resources,
                        R.drawable.ic_pause_button,
                        null
                    )
                )
            } else {
                binding.imageViewPlayPause.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        context.resources,
                        R.drawable.ic_play_button,
                        null
                    )
                )
            }
        }
    }

    private fun initializePlayer() {
        player = ExoPlayer.Builder(context)
            .build()
            .also { exoPlayer ->
                exoPlayer.playWhenReady = playWhenReady
                exoPlayer.seekTo(0, playbackPosition)
                exoPlayer.prepare()
            }
    }

    fun releasePlayer() {
        player?.run {
            playbackPosition = this.currentPosition
            currentWindow = this.currentWindowIndex
            playWhenReady = this.playWhenReady
            release()
        }
        player = null
    }

}