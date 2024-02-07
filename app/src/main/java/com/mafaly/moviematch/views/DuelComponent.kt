package com.mafaly.moviematch.views

import android.content.Context
import android.util.AttributeSet
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.mafaly.moviematchduel.BuildConfig
import com.mafaly.moviematchduel.R

class DuelComponent @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val imageView: ImageView
    private val titleTextView: TextView
    val choiceButton: Button

    init {
        inflate(context, R.layout.concurrent_duel_component, this)

        imageView = findViewById(R.id.concurrent_iv)
        titleTextView = findViewById(R.id.concurrent_title)
        choiceButton = findViewById(R.id.concurrent_choice_button)
    }

    fun setMovieDetails(movieTitle: String, moviePosterPath: String?) {
        Glide.with(this)
            .load(BuildConfig.TMDB_IMAGE_URL + moviePosterPath)
            //.error(R.drawable.error_image) // Ajoutez une image d'erreur si le chargement échoue
            .into(imageView)
        titleTextView.text = movieTitle
    }
}
