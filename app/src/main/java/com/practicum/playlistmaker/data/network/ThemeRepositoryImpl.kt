package com.practicum.playlistmaker.data.network

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.practicum.playlistmaker.domain.ThemeRepository


class ThemeRepositoryImpl (private val context: Context) : ThemeRepository {

    companion object {
        private const val APPLICATION_SETTINGS = "application_settings"
        private const val DARK_THEME_KEY = "dark_theme_key"
    }

    override fun saveTheme(darkTheme: Boolean) {
        val sharedPreferences = context.getSharedPreferences(APPLICATION_SETTINGS, MODE_PRIVATE)
        sharedPreferences.edit()
            .putBoolean(DARK_THEME_KEY, darkTheme)
            .apply()
    }

    override fun restoreTheme() : Boolean {
        val sharedPreferences = context.getSharedPreferences(APPLICATION_SETTINGS, MODE_PRIVATE)
        return sharedPreferences.getBoolean(DARK_THEME_KEY, false)
    }
}