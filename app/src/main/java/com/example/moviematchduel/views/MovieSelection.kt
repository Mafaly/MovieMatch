package com.example.moviematchduel.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moviematchduel.R
import com.example.moviematchduel.model.MovieDTO
import com.example.moviematchduel.views.adapters.MovieListAdapter
import com.example.moviematchduel.views.adapters.OnMovieClickedInMovieSelectionList
import com.example.moviematchduel.views.adapters.OnMovieDetailsClicked

class MovieSelection : AppCompatActivity(), OnMovieClickedInMovieSelectionList,
    OnMovieDetailsClicked {
    private lateinit var genreSpinner: Spinner
    private lateinit var platformSpinner: Spinner
    private lateinit var movieListRv: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_selection)

        genreSpinner = findViewById(R.id.genre_spr)
        ArrayAdapter.createFromResource(
            this,
            R.array.genre_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears.
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner.
            genreSpinner.adapter = adapter
        }

        platformSpinner = findViewById(R.id.platform_spr)
        ArrayAdapter.createFromResource(
            this,
            R.array.platform_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears.
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner.
            platformSpinner.adapter = adapter
        }
        movieListRv = findViewById(R.id.movie_list_rv)
        val movieList = generateRandomMovies()
        setUpMovieList(movieList)
    }

    private fun setUpMovieList(movies: List<MovieDTO>) {
        val movieAdapter = MovieListAdapter(movies, this, this)
        movieListRv.layoutManager = LinearLayoutManager(this)
        movieListRv.adapter = movieAdapter

    }

    override fun displayMovieSelectionConfirmationDialog(movieData: MovieDTO) {
        val text = "Afficher le dialog de confirmation de sélection du film"
        val duration = Toast.LENGTH_SHORT

        val toast = Toast.makeText(this, text, duration)
        toast.show()
    }

    override fun displayMovieDetails(movieData: MovieDTO) {
        val text = "Afficher toutes les informations du film sur le dialog"
        val duration = Toast.LENGTH_SHORT

        val toast = Toast.makeText(this, text, duration)
        toast.show()
    }

}

fun generateRandomMovies(): List<MovieDTO> {
    val movies = mutableListOf<MovieDTO>()
    for (i in 0..100) {
        movies.add(
            MovieDTO(
                i,
                "Movie $i",
                2000 + i,
                listOf("genre $i", "genre ${i + 1}", "genre ${i + 2}"),
                "https://robohash.org/Movie$i"
            )
        )
    }
    return movies
}