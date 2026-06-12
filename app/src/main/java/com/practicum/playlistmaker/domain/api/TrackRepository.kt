package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.Track

interface TrackRepository {
    fun search(expression: String): List<Track>
}