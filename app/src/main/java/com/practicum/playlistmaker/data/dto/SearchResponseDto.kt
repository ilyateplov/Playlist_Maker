package com.practicum.playlistmaker.data.dto

import com.practicum.playlistmaker.domain.Track

data class SearchResponseDto(
    val resultCount: Int,
    val results: List<Track>
) : Response()
