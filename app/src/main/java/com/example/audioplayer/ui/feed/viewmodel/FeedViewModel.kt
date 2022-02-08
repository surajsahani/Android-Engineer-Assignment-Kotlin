package com.example.audioplayer.ui.feed.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.audioplayer.data.Song
import com.example.audioplayer.network.SongRepository
import com.example.audioplayer.vo.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(private val songRepository: SongRepository) : ViewModel() {

    val songsResponse = MutableLiveData<Resource<ArrayList<Song>>>()

    fun getSongs() {
        songRepository.getSongs()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                songsResponse.value = Resource.loading(null)
            }
            .doOnError {
                songsResponse.value = Resource.error(it.message.toString(), null)
            }
            .subscribe {
                songsResponse.value = Resource.success(it.songs)
            }
    }
}