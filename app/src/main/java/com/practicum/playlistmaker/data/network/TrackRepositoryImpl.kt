package com.practicum.playlistmaker.data.network

import com.practicum.playlistmaker.data.NetworkClient
import com.practicum.playlistmaker.data.dto.SearchResponseDto
import com.practicum.playlistmaker.data.dto.TrackSearchRequest
import com.practicum.playlistmaker.domain.SearchResponse
import com.practicum.playlistmaker.domain.Track
import com.practicum.playlistmaker.domain.api.TrackRepository

class TrackRepositoryImpl(private val networkClient: NetworkClient) : TrackRepository {

    override fun search(expression: String): List<Track> {
        val response = networkClient.doRequest(TrackSearchRequest(expression))
        if (response.resultCode == 200) {
            return (response as SearchResponseDto).results.map {
                Track(
                    trackName = it.trackName,
                    artistName = it.artistName,
                    trackTimeMillis = it.trackTimeMillis,
                    artworkUrl100 = it.artworkUrl100,
                    collectionName = it.collectionName,
                    primaryGenreName = it.primaryGenreName,
                    releaseDate = it.releaseDate,
                    country = it.country,
                    previewUrl = it.previewUrl
                ) }
        } else {
            return emptyList()
        }
    }
}

