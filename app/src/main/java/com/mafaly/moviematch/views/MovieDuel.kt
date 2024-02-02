package com.mafaly.moviematch.views

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mafaly.moviematch.model.MovieDAO
import com.mafaly.moviematchduel.R

class MovieDuel : AppCompatActivity() {

    private var selectedMovieList: List<MovieDAO>? = null
    private var currentIndex = 0
    private lateinit var firstDuelComponent: DuelComponent
    private lateinit var secondDuelComponent: DuelComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_duel)

        firstDuelComponent = findViewById(R.id.first_concurrent_component)
        secondDuelComponent = findViewById(R.id.second_concurrent_component)

        selectedMovieList = generateSampleMovieList()

        displayMovieDetails(currentIndex)

        firstDuelComponent.choiceButton.setOnClickListener {
            currentIndex++
            if (currentIndex < selectedMovieList!!.size) {
                displayMovieDetails(currentIndex)
            } else {
                // All duels have been completed
                // final logic here
            }
        }
    }

    private fun displayMovieDetails(index: Int) {
        val movie = selectedMovieList!![index]
        firstDuelComponent.setMovieDetails(movie.title, 1)

        if (index + 1 < selectedMovieList!!.size) {
            val secondMovie = selectedMovieList!![index + 1]
            secondDuelComponent.setMovieDetails(secondMovie.title, 2)
        }
    }

    private fun generateSampleMovieList(): List<MovieDAO> {
        val movieList = mutableListOf<MovieDAO>()
        movieList.add(
            MovieDAO(
                1,
                "Movie 1",
                "2022",
                listOf("Action", "Adventure"),
                "path/to/poster1.jpg"
            )
        )
        movieList.add(
            MovieDAO(
                2,
                "Movie 2",
                "2022",
                listOf("Comedy", "Drama"),
                "path/to/poster2.jpg"
            )
        )
        movieList.add(
            MovieDAO(
                3,
                "Movie 3",
                "2022",
                listOf("Sci-Fi", "Thriller"),
                "path/to/poster3.jpg"
            )
        )
        return movieList
    }
}