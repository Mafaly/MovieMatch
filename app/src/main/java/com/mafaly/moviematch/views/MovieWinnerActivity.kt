package com.mafaly.moviematch.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.mafaly.moviematch.services.MovieService
import com.mafaly.moviematchduel.BuildConfig
import com.mafaly.moviematchduel.R
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class MovieWinnerActivity : AppCompatActivity() {
    private lateinit var movieTitleTv: TextView
    private lateinit var posterImageView: ImageView
    private lateinit var movieGenreTv: TextView
    private lateinit var descriptionButton: Button
    private lateinit var returnHomePageButton: Button
    private var movieId by Delegates.notNull<Long>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_winner)
        movieId = intent.getLongExtra("movieId", (-1).toLong())
        if (movieId == (-1).toLong()) {
            Toast.makeText(this, "Erreur: MovieId manquant", Toast.LENGTH_SHORT).show()
            finish()
        }

        lifecycleScope.launch {

            val movie = MovieService.getMovieById(this@MovieWinnerActivity, movieId)
            movieTitleTv = findViewById(R.id.movie_title_tv)
            posterImageView = findViewById(R.id.poster_iv)
            movieGenreTv = findViewById(R.id.movie_genre_tv)
            descriptionButton = findViewById(R.id.description_button)
            returnHomePageButton = findViewById(R.id.return_home_page_button)
            if (movie != null) {
                setMovieDetails(movie.movieTitle,movie.moviePosterPath,movie.movieGenre)
            }

        }
    }


    private fun setMovieDetails(movieTitle: String, moviePosterPath: String?,movieGenre : List<Int>?) {
        Glide.with(this)
            .load(BuildConfig.TMDB_IMAGE_URL + moviePosterPath)
            //.error(R.drawable.error_image) // Ajoutez une image d'erreur si le chargement échoue
            .into(posterImageView)
        movieTitleTv.text = movieTitle
        movieGenreTv.text=movieGenre.toString()
    }

}