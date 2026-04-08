package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.data.network.ThemeRepositoryImpl
import com.practicum.playlistmaker.domain.ThemeRepository
import com.practicum.playlistmaker.domain.api.ThemeInteractor

class ThemeInteractorImpl(private val themeRepository: ThemeRepository): ThemeInteractor {

    override fun saveTheme(darkTheme: Boolean) {
        themeRepository.saveTheme(darkTheme)
    }

    override fun restoreTheme() : Boolean {
        return themeRepository.restoreTheme()
    }
}