package com.mafaly.moviematch.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.mafaly.moviematch.model.MovieDAO
import com.mafaly.moviematch.repos.MovieGenre
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
                movie.movieGenre?.let { setMovieDetails(movie.movieTitle,movie.moviePosterPath, it) }
            }

            if (movie != null) {
                descriptionButton.setOnClickListener {
                    val movieDao = movie.toDAO()
                    displayMovieDetails(movieDao)
                }
            }

            returnHomePageButton.setOnClickListener{
                goToHomePage()
            }

        }
    }


    private fun setMovieDetails(movieTitle: String, moviePosterPath: String?,movieGenre : List<Int>) {
        Glide.with(this)
            .load(BuildConfig.TMDB_IMAGE_URL + moviePosterPath)
            .into(posterImageView)
        movieTitleTv.text = movieTitle
        movieGenreTv.text=
            movieGenre.let { MovieGenre.getGenreNames(movieGenre,this@MovieWinnerActivity).joinToString(", ") }
    }

    private fun displayMovieDetails(movieData: MovieDAO) {
        val detailsDialog = MovieDescriptionDialog()
        val movieJson: String = Gson().toJson(movieData)
        val args = Bundle()
        args.putString("movie", movieJson)
        detailsDialog.arguments = args
        detailsDialog.show(supportFragmentManager, "MovieDescriptionDialog")
    }
    private fun goToHomePage(){
        val intent = Intent(this, MovieMatchActivity::class.java)
        startActivity(intent)
    }

}