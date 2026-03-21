package com.practicum.playlistmaker

import android.app.DownloadManager.Request
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Adapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isInvisible
import androidx.core.view.isNotEmpty
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import com.practicum.playlistmaker.network.iTunesSearchService
import com.practicum.playlistmaker.search.ITunesSearchApi
import com.practicum.playlistmaker.search.SearchResponse
import com.practicum.playlistmaker.search.SearchState
import com.practicum.playlistmaker.search.Track
import com.practicum.playlistmaker.search.TrackAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Query


class SearchActivity : AppCompatActivity() {
    var request: String? = null


    private val onTrackClick: (Track) -> Unit = {track: Track ->
        if (historyTrackAdapter.tracks.contains(track)) {
            historyTrackAdapter.tracks.remove(track)
        }
        historyTrackAdapter.tracks.add(0,track)
        if (historyTrackAdapter.tracks.size > 10) {
            historyTrackAdapter.tracks.removeAt(10)
        }
        saveHistory(historyTrackAdapter.tracks)
        historyTrackAdapter.notifyDataSetChanged()

        val displayIntent = Intent(this@SearchActivity, TrackActivity::class.java)
        displayIntent.putExtra(TRACK_KEY, track)

        startActivity(displayIntent)

    }

    private lateinit var inputEditText: EditText

    private val trackAdapter = TrackAdapter(mutableListOf(), onTrackClick)

    private val historyTrackAdapter = TrackAdapter(mutableListOf(), onTrackClick)

    private var currentState: SearchState = SearchState.HISTORY

    private val gson: Gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(v.paddingLeft, systemBars.top, v.paddingRight, systemBars.bottom)
            insets
        }

        restoreHistory()

        val toolBar = findViewById<MaterialToolbar>(R.id.toolBar)

        toolBar.setNavigationOnClickListener {
            finish()
        }

        val linearLayout = findViewById<LinearLayout>(R.id.container)
        inputEditText = findViewById<EditText>(R.id.inputEditText)

        val clearButton = findViewById<ImageView>(R.id.clearIcon)
        val recyclerView = findViewById<RecyclerView>(R.id.trackList)
        val update = findViewById<MaterialButton>(R.id.update)
        val historyRecyclerView = findViewById<RecyclerView>(R.id.historyList)
        val clearHistory = findViewById<MaterialButton>(R.id.clear_history)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = trackAdapter

        historyRecyclerView.layoutManager = LinearLayoutManager(this)
        historyRecyclerView.adapter = historyTrackAdapter

        clearButton.setOnClickListener {
            inputEditText.setText("")
            this.currentFocus?.let { view ->
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(view.windowToken, 0)
            }
            trackAdapter.tracks.clear()
            trackAdapter.notifyDataSetChanged()
            switchState(SearchState.HISTORY)
        }

        clearHistory.setOnClickListener {
            historyTrackAdapter.tracks.clear()
            historyTrackAdapter.notifyDataSetChanged()
            saveHistory(emptyList())
            switchState(SearchState.LIST)
        }

        update.setOnClickListener {
            resultSearch(inputEditText.text.toString())
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.isVisible = !s.isNullOrEmpty()
                request = s?.toString()
                searchDebounce()
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }
        inputEditText.addTextChangedListener(simpleTextWatcher)

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                resultSearch(inputEditText.text.toString())
                true
            }
            false
        }
        switchState(SearchState.HISTORY)
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_REQUEST, request)
        outState.putSerializable(CURRENT_STATE, currentState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        request = savedInstanceState.getString(SEARCH_REQUEST)
        val inputEditText = findViewById<EditText>(R.id.inputEditText)
        inputEditText.setText(request)
        currentState = savedInstanceState.getSerializable(CURRENT_STATE, SearchState::class.java) ?: SearchState.HISTORY
        switchState(currentState)
    }

    fun switchState(state: SearchState) {
        val nothingFound = findViewById<LinearLayout>(R.id.nothingFound)
        val communicationProblem = findViewById<LinearLayout>(R.id.communicationProblem)
        val trackList = findViewById<RecyclerView>(R.id.trackList)
        val historyList = findViewById<LinearLayout>(R.id.searchHistory)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        trackList.isVisible = state == SearchState.LIST
        nothingFound.isVisible = state == SearchState.EMPTY
        communicationProblem.isVisible = state == SearchState.ERROR
        historyList.isVisible = state == SearchState.HISTORY && historyTrackAdapter.tracks.isNotEmpty()
        progressBar.isVisible = state == SearchState.LOADING
        currentState = state
    }

    fun resultSearch(query: String) {
        if (query.isNotEmpty()) {


            switchState(SearchState.LOADING)

            iTunesSearchService.search(query)
                .enqueue(object : Callback<SearchResponse> {
                    override fun onResponse(
                        call: Call<SearchResponse>,
                        response: Response<SearchResponse>
                    ) {

                        if (response.code() == 200) {
                            trackAdapter.tracks.clear()
                            if (response.body()?.results?.isNotEmpty() == true) {
                                trackAdapter.tracks.addAll(response.body()?.results!!)
                                trackAdapter.notifyDataSetChanged()
                            }
                            if (trackAdapter.tracks.isEmpty()) {
                                switchState(SearchState.EMPTY)
                            } else {
                                switchState(SearchState.LIST)
                            }
                        } else {
                            switchState(SearchState.ERROR)
                        }
                    }

                    override fun onFailure(call: Call<SearchResponse>, t: Throwable) {

                        switchState(SearchState.ERROR)
                    }

                })
        }
    }


    fun saveHistory(tracks: List<Track>) {
        val sharedPreferences = getSharedPreferences(PRACTICUM_HOMEWORK, MODE_PRIVATE)
        sharedPreferences.edit()
            .putString(TRACK_HISTORY_KEY, gson.toJson(tracks))
            .apply()
    }

    fun restoreHistory() {
        val sharedPreferences = getSharedPreferences(PRACTICUM_HOMEWORK, MODE_PRIVATE)
        val json = sharedPreferences.getString(TRACK_HISTORY_KEY, "[]")
        val tracks = gson.fromJson(json, Array<Track>::class.java).asList()
        historyTrackAdapter.tracks.addAll(tracks)
        historyTrackAdapter.notifyDataSetChanged()
    }
    companion object {
        const val SEARCH_REQUEST = "SEARCH_REQUEST"
        const val CURRENT_STATE = "CURRENT_STATE"

        const val PRACTICUM_HOMEWORK = "practicum_homework"
        const val TRACK_HISTORY_KEY = "key_for_track_history"

        const val TRACK_KEY = "key_for_track"

        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    private val searchRunnable = Runnable { resultSearch(query = inputEditText.text.toString()) }

    private val handler = Handler(Looper.getMainLooper())

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }
}




