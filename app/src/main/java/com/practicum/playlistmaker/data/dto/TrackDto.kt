package com.practicum.playlistmaker.data.dto

data class TrackDto(
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: String,
    val artworkUrl100: String,
    val collectionName: String?,
    val primaryGenreName: String?,
    val releaseDate: String?,
    val country: String?,
    var previewUrl: String?)
