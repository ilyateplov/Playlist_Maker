package com.practicum.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.google.gson.Gson
import com.practicum.playlistmaker.SearchActivity.Companion.PRACTICUM_HOMEWORK
import com.practicum.playlistmaker.SearchActivity.Companion.TRACK_HISTORY_KEY
import com.practicum.playlistmaker.search.Track

class App : Application() {

    var darkTheme = false

    override fun onCreate() {
        super.onCreate()
        restoreTheme()
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
        saveTheme(darkTheme)
    }

    fun saveTheme(darkTheme: Boolean) {
        val sharedPreferences = getSharedPreferences(APPLICATION_SETTINGS, MODE_PRIVATE)
        sharedPreferences.edit()
            .putBoolean(DARK_THEME_KEY, darkTheme)
            .apply()
    }

    fun restoreTheme() {
        val sharedPreferences = getSharedPreferences(APPLICATION_SETTINGS, MODE_PRIVATE)
        switchTheme(sharedPreferences.getBoolean(DARK_THEME_KEY, false))
    }

    companion object {
        const val APPLICATION_SETTINGS = "application_settings"
        const val DARK_THEME_KEY = "dark_theme_key"
    }
}

