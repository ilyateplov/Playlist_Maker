package com.practicum.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate



class App : Application() {
    val themeInteractor = Creator.provideThemeInteractor(this)
    var darkTheme = false
        private set

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
        themeInteractor.saveTheme(darkTheme)
    }

    fun restoreTheme() : Boolean {
        val restoreTheme = themeInteractor.restoreTheme()
        return restoreTheme
        switchTheme(restoreTheme)
    }
}

