package com.example.audioplayer.network

import com.example.audioplayer.data.SongsResponse
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class SongRepository @Inject constructor(private val songService: SongService) {

    fun getSongs(): Observable<SongsResponse> {
        return songService.getSongs()
    }
}