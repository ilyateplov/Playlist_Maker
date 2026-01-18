package com.practicum.playlistmaker.search

import java.io.Serializable

data class Track(
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: String,
    val artworkUrl100: String,
    val collectionName: String?,
    val primaryGenreName: String?,
    val releaseDate: String?,
    val country: String?
) : Serializable {
    fun getCoverArtwork() = artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")

    fun getYear(): Int? {
        if ((releaseDate != null) && (releaseDate.length >= 4)) {
            return releaseDate.take(4).toInt()
        } else {
            return null
        }
    }
}


