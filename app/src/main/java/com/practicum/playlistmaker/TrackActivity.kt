package com.practicum.playlistmaker

import android.icu.text.SimpleDateFormat
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.appbar.MaterialToolbar
import com.practicum.playlistmaker.SearchActivity.Companion.TRACK_KEY
import com.practicum.playlistmaker.search.Track
import java.util.Locale

class TrackActivity : AppCompatActivity(){

    private var mediaPlayer = MediaPlayer()
    private lateinit var play: ImageView

    private lateinit var trackTime: TextView

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_track)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.container)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(v.paddingLeft, systemBars.top, v.paddingRight, systemBars.bottom)
            insets
        }

        val toolBar = findViewById<MaterialToolbar>(R.id.toolBar)

        toolBar.setNavigationOnClickListener {
            finish()
        }

        val track: Track = getIntent().getSerializableExtra(TRACK_KEY) as Track

        play = findViewById<ImageView>(R.id.action_play)
        trackTime = findViewById<TextView>(R.id.track_time)

        preparePlayer(url = track.previewUrl.orEmpty())

        play.setOnClickListener {
            playbackControl()
        }

        val cover = findViewById<ImageView>(R.id.cover)
        val transformations = MultiTransformation(CenterCrop(), RoundedCorners(resources.getDimension(R.dimen.track_cover_corner).toInt()))
        Glide.with(this)
            .load(track.getCoverArtwork())
            .apply(RequestOptions.bitmapTransform(transformations))
            .placeholder(R.drawable.ic_placeholder_45)
            .into(cover)

        val trackName = findViewById<TextView>(R.id.track_name)
        trackName.text = track.trackName

        val trackArtist = findViewById<TextView>(R.id.track_artist)
        trackArtist.text = track.artistName



        val trackDurationValue = findViewById<TextView>(R.id.track_duration_value)
        trackDurationValue.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis.toLong())

        val trackAlbumValue = findViewById<TextView>(R.id.track_album_value)
        val trackAlbumTitle = findViewById<TextView>(R.id.track_album_title)
        trackAlbumValue.text = track.collectionName
        if (track.collectionName.isNullOrEmpty()) {
            trackAlbumTitle.isVisible = false
            trackAlbumValue.isVisible = false
        }

        val trackYearValue = findViewById<TextView>(R.id.track_year_value)
        val trackYearTitle = findViewById<TextView>(R.id.track_year_title)
        trackYearValue.text = track.getYear()?.toString()
        if (track.releaseDate.isNullOrEmpty()) {
            trackYearTitle.isVisible = false
            trackYearValue.isVisible = false
        }

        val trackGenreValue = findViewById<TextView>(R.id.track_genre_value)
        val trackGenreTitle = findViewById<TextView>(R.id.track_genre_title)
        trackGenreValue.text = track.primaryGenreName
        if (track.primaryGenreName.isNullOrEmpty()) {
            trackGenreTitle.isVisible = false
            trackGenreValue.isVisible = false
        }

        val trackCountryValue = findViewById<TextView>(R.id.track_country_value)
        val trackCountryTitle = findViewById<TextView>(R.id.track_country_title)
        trackCountryValue.text = track.country
        if (track.country.isNullOrEmpty()) {
            trackCountryTitle.isVisible = false
            trackCountryValue.isVisible = false
        }

    }

    private fun preparePlayer(url: String) {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            play.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            play.setImageResource(R.drawable.ic_play_100)
            playerState = STATE_PREPARED
            mediaPlayer.seekTo(0)
        }
        checkTimePosition()

    }

    private fun startPlayer() {
        mediaPlayer.start()
        play.setImageResource(R.drawable.ic_pause_100)
        playerState = STATE_PLAYING
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        play.setImageResource(R.drawable.ic_play_100)
        playerState = STATE_PAUSED
    }

    private fun playbackControl() {
        when(playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeMessages(0)
        mediaPlayer.release()
    }
    fun checkTimePosition() {
        trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
        handler.postDelayed({ checkTimePosition() }, CHECK_TIME_DELAY)

    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val CHECK_TIME_DELAY = 1000L
    }

    private var playerState = STATE_DEFAULT
}