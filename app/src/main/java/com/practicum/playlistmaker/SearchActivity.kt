package com.practicum.playlistmaker

import android.app.DownloadManager.Request
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Adapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.practicum.playlistmaker.search.ITunesSearchApi
import com.practicum.playlistmaker.search.SearchResponse
import com.practicum.playlistmaker.search.TrackAdapter
import com.practicum.playlistmaker.search.tracks
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {
    var request: String? = null

    private val iTunesSearchBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesSearchBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesSearchService = retrofit.create(ITunesSearchApi::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(v.paddingLeft, systemBars.top, v.paddingRight, systemBars.bottom)
            insets
        }

        val toolBar = findViewById<MaterialToolbar>(R.id.toolBar)

        toolBar.setNavigationOnClickListener {
            finish()
        }

        val linearLayout = findViewById<LinearLayout>(R.id.container)
        val inputEditText = findViewById<EditText>(R.id.inputEditText)
        val clearButton = findViewById<ImageView>(R.id.clearIcon)
        val recyclerView = findViewById<RecyclerView>(R.id.trackList)

        recyclerView.layoutManager = LinearLayoutManager(this)
        val trackAdapter = TrackAdapter(tracks)
        recyclerView.adapter = trackAdapter

        clearButton.setOnClickListener {
            inputEditText.setText("")
            this.currentFocus?.let { view ->
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.isVisible = !s.isNullOrEmpty()
                request = s?.toString()
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }
        inputEditText.addTextChangedListener(simpleTextWatcher)

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (inputEditText.text.isNotEmpty()) {
                    iTunesSearchService.search(inputEditText.text.toString())
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
                                        //showMessage(getString(R.string.nothing_found), "")
                                    } else {
                                        //showMessage("", "")
                                    }
                                } else {
                                    //showMessage(getString(R.string.something_went_wrong), response.code().toString())
                                }
                            }

                            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                                //showMessage(
                                    //getString(R.string.something_went_wrong),
                                    //t.message.toString()
                                //)
                            }

                        })
                }
                true
            }
            false
        }
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_REQUEST, request)

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        request = savedInstanceState.getString(SEARCH_REQUEST)
        val inputEditText = findViewById<EditText>(R.id.inputEditText)
        inputEditText.setText(request)
    }

    companion object {
        const val SEARCH_REQUEST = "SEARCH_REQUEST"
    }
}

