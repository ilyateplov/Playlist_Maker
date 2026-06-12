package com.practicum.playlistmaker.domain

interface HistoryRepository {
    fun saveHistory(tracks: List<Track>)
    fun restoreHistory() : List<Track>
}