package com.practicum.playlistmaker.search

import java.io.Serializable

enum class SearchState: Serializable{
    LIST, EMPTY, ERROR, HISTORY, LOADING
}