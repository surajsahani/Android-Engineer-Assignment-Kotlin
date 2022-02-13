package com.example.audioplayer.ui.feed

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.example.audioplayer.R
import com.example.audioplayer.data.Song
import com.example.audioplayer.databinding.ActivityMainBinding
import com.example.audioplayer.ui.base.BaseActivity
import com.example.audioplayer.ui.feed.adapter.SongAdapter
import com.example.audioplayer.ui.feed.viewmodel.FeedViewModel
import com.example.audioplayer.util.gone
import com.example.audioplayer.util.show
import com.example.audioplayer.vo.Status
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var binding: ActivityMainBinding

    lateinit var songAdapter: SongAdapter

    private val viewModel: FeedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupFeed()
        fetchSongs()
        observeSongsResponse()
    }

    private fun observeSongsResponse() {
        viewModel.songsResponse.observe(this) { resource ->
            when (resource.status) {
                Status.ERROR -> {
                    Log.e(TAG, "observeSongsResponse: ${resource.message}")
                }
                Status.SUCCESS -> {
                    resource.data?.let { songs ->
                        if (!songs.isNullOrEmpty()) {
                            binding.progressBar.gone()
                            Log.d(TAG, "Fetched songs (${songs.size}) = $songs")
                            songAdapter.data = songs
                        }
                    }
                }
                Status.LOADING -> {
                    binding.progressBar.show()
                }
            }
        }
    }

    private fun fetchSongs() {
        viewModel.getSongs()
    }

    private fun setupFeed() {
        songAdapter = SongAdapter(this) { view: View?, song: Song ->
            handleEvent(view, song)

        }
        binding.viewPagerFeed.apply {
            adapter = songAdapter
            // TODO: 15-11-2021 Messes UI
//            setPageTransformer(DepthPageTransformer())
            registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {

                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    // TODO: Loading the item each time page is loaded, can be optimized
                    songAdapter.notifyItemChanged(position)
                }
            })
        }
    }

    private fun handleEvent(view: View?, song: Song) {
        when (view?.id) {
            R.id.buttonShare -> shareSong(song)
            R.id.buttonDownload -> downloadSong(song)
            R.id.buttonVolume -> controlVolume()
            R.id.buttonFavorite -> addSongToFavorites(song)
            R.id.progressBar -> moveToNextSong(song)
        }
    }

    private fun moveToNextSong(song: Song) {
        Log.d(TAG, "moveToNextSong: curr: ${binding.viewPagerFeed.currentItem}")
        if (songAdapter.data.size == binding.viewPagerFeed.currentItem + 1) {
            // it is last song
            return
        } else {
            binding.viewPagerFeed.setCurrentItem(binding.viewPagerFeed.currentItem + 1, true)
            songAdapter.player?.seekTo(0)
        }
    }

    private fun addSongToFavorites(song: Song) {
        if (song.isFavorite) {
            showMessage(getString(R.string.message_song_added_to_favorites))
        } else {
            showMessage(getString(R.string.message_song_removed_from_favorites))
        }
    }

    private fun controlVolume() {


    }

    private fun downloadSong(song: Song) {
        // TODO: 16-11-2021
        showMessage(getString(R.string.message_song_is_downloading))
    }

    private fun shareSong(song: Song) {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.putExtra(
            Intent.EXTRA_TEXT,
            "Hey, I am listening to ${song.title} " +
                    "by ${song.creator.email} " +
                    "on ${getString(R.string.app_name)}"
        )
        shareIntent.type = "text/plain"
        startActivity(Intent.createChooser(shareIntent, "send to"))
    }

    override fun onStop() {
        super.onStop()
        songAdapter.releasePlayer()
    }
}