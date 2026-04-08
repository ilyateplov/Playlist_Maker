package com.practicum.playlistmaker.domain

import android.content.Context.MODE_PRIVATE


interface ThemeRepository {
    fun saveTheme(darkTheme: Boolean)

    fun restoreTheme() : Boolean
}