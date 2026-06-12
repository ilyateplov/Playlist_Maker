package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.Track

interface TrackInteractor {
    fun search(expression: String, consumer: TrackConsumer)

    interface TrackConsumer {
        fun consume(foundTrack: List<Track>)
    }
}