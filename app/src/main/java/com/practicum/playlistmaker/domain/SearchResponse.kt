package com.practicum.playlistmaker.domain

import com.practicum.playlistmaker.data.dto.Response
import com.practicum.playlistmaker.data.dto.TrackDto
import com.practicum.playlistmaker.domain.Track

class SearchResponse (
    val resultCount: Int,
    val results: List<TrackDto>)