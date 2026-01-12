package com.practicum.playlistmaker.network

import com.practicum.playlistmaker.search.ITunesSearchApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private val iTunesSearchBaseUrl = "https://itunes.apple.com"

private val retrofit = Retrofit.Builder()
    .baseUrl(iTunesSearchBaseUrl)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

val iTunesSearchService = retrofit.create(ITunesSearchApi::class.java)