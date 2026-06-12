package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.HistoryRepository
import com.practicum.playlistmaker.domain.Track
import com.practicum.playlistmaker.domain.api.HistoryInteractor

class HistoryInteractorImpl(private val historyRepository: HistoryRepository) :
    HistoryInteractor {

    override fun saveHistory(tracks: List<Track>) {
        historyRepository.saveHistory(tracks)
    }

    override fun restoreHistory(): List<Track> {
        return historyRepository.restoreHistory()
    }
}
