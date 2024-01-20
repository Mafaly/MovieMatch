package com.mafaly.moviematch.views

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mafaly.moviematch.di.injectModuleDependencies
import com.mafaly.moviematch.di.parseConfigurationAndAddItToInjectionModules
import com.mafaly.moviematch.model.MovieDAO
import com.mafaly.moviematch.repo.MovieViewModel
import com.mafaly.moviematch.views.adapters.MovieListAdapter
import com.mafaly.moviematch.views.adapters.OnMovieClickedInMovieSelectionList
import com.mafaly.moviematch.views.adapters.OnMovieDetailsClicked
import com.mafaly.moviematchduel.R
import org.koin.androidx.viewmodel.ext.android.viewModel

class MovieSelection : AppCompatActivity(), OnMovieClickedInMovieSelectionList,
    OnMovieDetailsClicked {

    private val movieViewModel: MovieViewModel by viewModel()

    private lateinit var genreSpinner: Spinner
    private lateinit var platformSpinner: Spinner
    private lateinit var movieListRv: RecyclerView
    private lateinit var randomMovieButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Assign the layout to the activity
        setContentView(R.layout.activity_movie_selection)

        // Binding the variables to the views
        genreSpinner = findViewById(R.id.genre_spr)
        ArrayAdapter.createFromResource(
            this, R.array.genre_array, android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears.
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner.
            genreSpinner.adapter = adapter
        }
        platformSpinner = findViewById(R.id.platform_spr)
        ArrayAdapter.createFromResource(
            this, R.array.platform_array, android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears.
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner.
            platformSpinner.adapter = adapter
        }
        movieListRv = findViewById(R.id.movie_list_rv)
        randomMovieButton = findViewById(R.id.random_search_btn)

        // binding interaction to the buttons
        randomMovieButton.setOnClickListener {
            displayUpcomingMovies()
        }

        // Dependency injection
        parseConfigurationAndAddItToInjectionModules()
        injectModuleDependencies(this@MovieSelection)

        // Observe the movie data from the view model
        this.observeMovieLiveData()
    }

    private fun observeMovieLiveData() {
        movieViewModel.movieLiveData.observe(this@MovieSelection) { movieList ->
            Toast.makeText(
                this, "Got data about ${movieList.size} random movies", Toast.LENGTH_LONG
            ).show()
            setUpMovieListViews(movieList)
        }
    }


    private fun setUpMovieListViews(movies: List<MovieDAO>) {
        val movieAdapter = MovieListAdapter(movies, this, this)
        movieListRv.layoutManager = LinearLayoutManager(this)
        movieListRv.adapter = movieAdapter

    }

    private fun displayUpcomingMovies() {
        movieViewModel.getUpcomingMovies()
    }

    // Implementing the interaction interface methods
    override fun displayMovieSelectionConfirmationDialog(movieData: MovieDAO) {
        val text = "Afficher le dialog de confirmation de sélection du film ${movieData.title}"
        val duration = Toast.LENGTH_SHORT

        val toast = Toast.makeText(this, text, duration)
        toast.show()
    }

    override fun displayMovieDetails(movieData: MovieDAO) {
        val text = "Afficher toutes les informations du film ${movieData.title} sur le dialog"
        val duration = Toast.LENGTH_SHORT

        val toast = Toast.makeText(this, text, duration)
        toast.show()
    }

}
