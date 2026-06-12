package com.practicum.playlistmaker

import android.content.Context
import com.google.gson.Gson
import com.practicum.playlistmaker.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.data.network.HistoryRepositoryImpl
import com.practicum.playlistmaker.data.network.ITunesSearchApi
import com.practicum.playlistmaker.data.network.ThemeRepositoryImpl
import com.practicum.playlistmaker.data.network.TrackRepositoryImpl
import com.practicum.playlistmaker.domain.HistoryRepository
import com.practicum.playlistmaker.domain.api.HistoryInteractor
import com.practicum.playlistmaker.domain.api.ThemeInteractor
import com.practicum.playlistmaker.domain.api.TrackInteractor
import com.practicum.playlistmaker.domain.api.TrackRepository
import com.practicum.playlistmaker.domain.impl.HistoryInteractorImpl
import com.practicum.playlistmaker.domain.impl.ThemeInteractorImpl
import com.practicum.playlistmaker.domain.impl.TrackInteractorImpl
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Creator {
    private fun getTrackRepository(): TrackRepository {
        val iTunesSearchService = provideITunesSearchService()
        return TrackRepositoryImpl(RetrofitNetworkClient(iTunesSearchService))
    }

    fun provideTrackInteractor(): TrackInteractor {
        return TrackInteractorImpl(getTrackRepository())
    }

    private fun getSaveHistoryRepository(context: Context, gson: Gson): HistoryRepository{
        return HistoryRepositoryImpl(context, gson)
    }

    fun provideSaveHistoryInteractor(context: Context, gson: Gson): HistoryInteractor {
        return HistoryInteractorImpl(getSaveHistoryRepository(context, gson))
    }

    private fun getThemeRepository(context: Context): ThemeRepositoryImpl {
        return ThemeRepositoryImpl(context)
    }

    fun provideThemeInteractor(context: Context): ThemeInteractor {
        return ThemeInteractorImpl(getThemeRepository(context))
    }

    private fun provideITunesSearchService(): ITunesSearchApi {
        val iTunesSearchBaseUrl = "https://itunes.apple.com"
        val retrofit = Retrofit.Builder()
            .baseUrl(iTunesSearchBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(ITunesSearchApi::class.java)
    }

    fun createGson(): Gson {
        return Gson()
    }

}


