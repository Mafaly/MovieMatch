package com.mafaly.moviematch.views

import android.content.Context
import android.util.AttributeSet
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.mafaly.moviematchduel.R

class DuelComponent @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val imageView: ImageView
    private val titleTextView: TextView
    private val choiceButton: Button

    init {
        inflate(context, R.layout.concurrent_duel_component, this)

        imageView = findViewById(R.id.concurrent_iv)
        titleTextView = findViewById(R.id.concurrent_title)
        choiceButton = findViewById(R.id.concurrent_choice_button)
    }

    fun setMovieDetails(movieTitle: String, moviePosterPath: String?) {
        imageView.setImageResource(R.drawable.poster_film_land)
        titleTextView.text = movieTitle
    }
}
