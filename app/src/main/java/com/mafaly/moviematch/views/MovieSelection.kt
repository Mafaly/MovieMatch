package com.mafaly.moviematch.views

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.mafaly.moviematch.di.injectModuleDependencies
import com.mafaly.moviematch.di.parseConfigurationAndAddItToInjectionModules
import com.mafaly.moviematch.game.GameManager
import com.mafaly.moviematch.model.MovieDAO
import com.mafaly.moviematch.repo.MovieViewModel
import com.mafaly.moviematch.repos.MovieGenre
import com.mafaly.moviematch.views.adapters.MovieListAdapter
import com.mafaly.moviematch.views.adapters.OnMovieClickedInMovieSelectionList
import com.mafaly.moviematch.views.adapters.OnMovieDetailsClicked
import com.mafaly.moviematch.views.adapters.OnSelectedMovieLongClicked
import com.mafaly.moviematch.views.adapters.OnSelectedMovieSimpleClicked
import com.mafaly.moviematch.views.adapters.SelectedMovieListAdapter
import com.mafaly.moviematchduel.R
import org.koin.androidx.viewmodel.ext.android.viewModel

class MovieSelection : AppCompatActivity(), OnMovieClickedInMovieSelectionList,
    OnMovieDetailsClicked, OnSelectedMovieLongClicked, OnSelectedMovieSimpleClicked {

    private val movieViewModel: MovieViewModel by viewModel()

    private lateinit var movieListRv: RecyclerView
    private lateinit var selectedMovieListRv: RecyclerView
    private lateinit var upcomingMovieSarchButton: Button
    private lateinit var popularMovieSarchButton: Button
    private lateinit var genresChipGroup: ChipGroup
    private lateinit var searchEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Assign the layout to the activity
        setContentView(R.layout.activity_movie_selection)

        val gameEntity = GameManager.getInstance().getCurrentGame()
        if (gameEntity == null) {
            Toast.makeText(this, "Erreur: GameEntity manquante", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            //TODO ce code temporaire permet d'enregister la partie en BDD comme si c'était la fin du jeu
            // Utiliser GameManager.getInstance().getCurrentGame() pour récup la partie en cours si besoin.
            GameManager.getInstance().finishCurrentGame(applicationContext, this, gameEntity)
        }

        // map over the MovieGenre map to get the genre names
        genresChipGroup = findViewById(R.id.genre_cp_grp)
        // map over the MovieGenre map to get the genre names into chips
        MovieGenre.genreList.map { it.value }.forEach { genreName ->
            val chip = Chip(this)
            chip.text = genreName
            chip.isCheckable = true
            genresChipGroup.addView(chip)
        }

        movieListRv = findViewById(R.id.movie_list_rv)
        selectedMovieListRv = findViewById(R.id.selected_movie_list_rv)
        popularMovieSarchButton = findViewById(R.id.popular_search_btn)
        upcomingMovieSarchButton = findViewById(R.id.upcoming_search_btn)
        searchEditText = findViewById(R.id.movie_search_et)

        setupFiltersBehavior()
        setupSearchBehavior()
        setupUpcomingSearchBehavior()
        setupPopularSearchBehavior()

        // Dependency injection
        parseConfigurationAndAddItToInjectionModules()
        injectModuleDependencies(this@MovieSelection)

        // Observe the movie data from the view model
        this.observeMovieLiveData()
    }

    private fun observeMovieLiveData() {
        movieViewModel.movieLiveData.observe(this@MovieSelection) { movieList ->
            setUpMovieListViews(movieList)
        }
        movieViewModel.selectedMovieLiveData.observe(this@MovieSelection) { movieList ->
            setUpSelectedMovieListViews(movieList)
        }
    }

    private fun setUpMovieListViews(movies: List<MovieDAO>) {
        val movieAdapter = MovieListAdapter(movies, this, this)
        movieListRv.layoutManager = LinearLayoutManager(this)
        movieListRv.adapter = movieAdapter

    }

    private fun setUpSelectedMovieListViews(movies: List<MovieDAO>) {
        val selectedMovieAdapter = SelectedMovieListAdapter(movies, this, this)
        selectedMovieListRv.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        selectedMovieListRv.adapter = selectedMovieAdapter

    }

    private fun displayUpcomingMovies() {
        movieViewModel.getUpcomingMovies()
    }

    private fun displayPopularMovies() {
        movieViewModel.getPopularMoviesInfos()
    }

    // Implementing the interaction interface methods
    override fun displayMovieSelectionConfirmationDialog(movieData: MovieDAO) {
        // TODO Afficher le dialog de confirmation de sélection du film
        val text = "Afficher le dialog de confirmation de sélection du film ${movieData.title}"
        val duration = Toast.LENGTH_SHORT

        val toast = Toast.makeText(this, text, duration)
        toast.show()
        movieViewModel.addMovieToSelectedList(movieData)
    }

    override fun displayMovieDetails(movieData: MovieDAO) {
        val text = "Afficher toutes les informations du film ${movieData.title} sur le dialog"
        val duration = Toast.LENGTH_SHORT

        val toast = Toast.makeText(this, text, duration)
        toast.show()
    }

    private fun setupSearchBehavior() {
        // Delay the search for movies until the user has stopped typing to prevent too many requests on the Data source
        searchEditText.addTextChangedListener(object : TextWatcher {
            private val handler = Handler(Looper.getMainLooper())
            private val debouncePeriod: Long = 500
            override fun afterTextChanged(s: Editable?) {
                handler.removeCallbacksAndMessages(null)
                handler.postDelayed({
                    if (searchEditText.isEnabled && searchEditText.text.toString().length > 2 || genresChipGroup.checkedChipIds.isNotEmpty()) {
                        movieViewModel.searchForMovies(getQuery())
                    }
                }, debouncePeriod)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }

    private fun setupFiltersBehavior() {
        genresChipGroup.setOnCheckedStateChangeListener { chipGroup, _ ->
            searchEditText.isEnabled = chipGroup.checkedChipIds.isEmpty()
        }
    }

    private fun setupUpcomingSearchBehavior() {
        upcomingMovieSarchButton.setOnClickListener {
            displayUpcomingMovies()
        }
    }

    private fun setupPopularSearchBehavior() {
        popularMovieSarchButton.setOnClickListener {
            displayPopularMovies()
        }
    }

    private fun getQuery(): String {
        return searchEditText.text.toString()
    }

    override fun removeFromSelection(movie: MovieDAO) {
        this.movieViewModel.removeFromSelection(movie)
    }
}
