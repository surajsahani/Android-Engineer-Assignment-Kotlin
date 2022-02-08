package com.example.audioplayer.data


import com.google.gson.annotations.SerializedName

data class SongsResponse(
    @SerializedName("shorts")
    val songs: ArrayList<Song>
)

data class Song(
    @SerializedName("shortID")
    val shortId: String,

    @SerializedName("title")
    val title: String,

    @SerializedName("dateCreated")
    val dateCreated: String,

    @SerializedName("audioPath")
    val audioUrl: String,

    @SerializedName("creator")
    val creator: Creator,

    var isFavorite : Boolean = false
) {
    data class Creator(
        @SerializedName("userID")
        val userId: String,

        @SerializedName("email")
        val email: String
    )
}