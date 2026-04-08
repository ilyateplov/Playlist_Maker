package com.practicum.playlistmaker.data.network

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.google.gson.Gson
import com.practicum.playlistmaker.domain.HistoryRepository
import com.practicum.playlistmaker.domain.Track

class HistoryRepositoryImpl(private val context: Context, private val gson: Gson) : HistoryRepository {

    companion object{
        const val PRACTICUM_HOMEWORK = "practicum_homework"
        const val TRACK_HISTORY_KEY = "key_for_track_history"
    }

    override fun saveHistory(tracks: List<Track>) {
        val sharedPreferences = context.getSharedPreferences(PRACTICUM_HOMEWORK, MODE_PRIVATE)
        sharedPreferences.edit()
            .putString(TRACK_HISTORY_KEY, gson.toJson(tracks))
            .apply()
    }

    override fun restoreHistory() : List<Track> {
        val sharedPreferences = context.getSharedPreferences(PRACTICUM_HOMEWORK, MODE_PRIVATE)
        val json = sharedPreferences.getString(TRACK_HISTORY_KEY, "[]")
        val tracks = gson.fromJson(json, Array<Track>::class.java).asList()
        return tracks
    }
}

