package com.practicum.playlistmaker.domain.api

interface ThemeInteractor {
    fun saveTheme(darkTheme: Boolean)

    fun restoreTheme() : Boolean
}