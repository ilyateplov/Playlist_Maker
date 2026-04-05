package com.practicum.playlistmaker.ui

import java.io.Serializable

enum class SearchState: Serializable {
    LIST, EMPTY, ERROR, HISTORY, LOADING
}