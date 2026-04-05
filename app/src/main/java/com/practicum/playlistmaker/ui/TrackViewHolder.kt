package com.practicum.playlistmaker.ui

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.Track
import java.util.Locale

class TrackViewHolder(
    val parent:ViewGroup,
    parentView : View = LayoutInflater.from(parent.context).inflate(R.layout.item_track, parent, false)
) : RecyclerView.ViewHolder(parentView) {

    private val cover: ImageView = parentView.findViewById(R.id.cover)
    private val title: TextView = parentView.findViewById(R.id.title)
    private val subtitle: TextView = parentView.findViewById(R.id.subtitle)
    private val time: TextView = parentView.findViewById(R.id.time)

    private val container: LinearLayout = parentView.findViewById(R.id.container)

    fun bind(model: Track, onTrackClick: (Track) -> Unit) {
        title.text = model.trackName
        subtitle.text = model.artistName
        time.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(model.trackTimeMillis.toLong())

        container.setOnClickListener {
            onTrackClick(model)
        }

        Glide.with(itemView)
            .load(model.artworkUrl100)
            .transform(RoundedCorners(convertDpToPixels(parent.context, 2f).toInt()))
            .placeholder(R.drawable.ic_placeholder_45)
            .fitCenter()
            .into(cover)
    }
}

fun convertDpToPixels(context: Context, dp: Float) = dp * context.resources.displayMetrics.density