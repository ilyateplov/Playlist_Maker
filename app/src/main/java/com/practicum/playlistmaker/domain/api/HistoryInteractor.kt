package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.Track

interface HistoryInteractor {
    fun saveHistory(tracks: List<Track>)

    fun restoreHistory() : List<Track>
}