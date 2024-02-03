package com.mafaly.moviematch.views

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.gson.Gson
import com.mafaly.moviematch.di.injectModuleDependencies
import com.mafaly.moviematch.di.parseConfigurationAndAddItToInjectionModules
import com.mafaly.moviematch.game.GameManager
import com.mafaly.moviematch.model.MovieDAO
import com.mafaly.moviematch.repo.MovieViewModel
import com.mafaly.moviematch.repos.MovieGenre
import com.mafaly.moviematch.repos.MovieWatchProviders
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
    private lateinit var genresChipGroup: ChipGroup
    private lateinit var watchProviderChipGroup: ChipGroup
    private lateinit var searchEditText: EditText
    private lateinit var searchWithFilterButton: Button
    private lateinit var clearFiltersImageButton: ImageButton

    @SuppressLint("DiscouragedApi")
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
        MovieGenre.genreList.map { it }.forEach { genre ->
            val chip = Chip(this)
            // get from string resouce
            chip.text = resources.getIdentifier(genre.value, "string", packageName).let {
                resources.getString(it)
            }
            chip.isCheckable = true
            chip.id = genre.key
            genresChipGroup.addView(chip)
        }

        // map over the MovieWatchProviders map to get the genre names
        watchProviderChipGroup = findViewById(R.id.watch_providers_cp_grp)
        // map over the MovieGenre map to get the genre names into chips
        MovieWatchProviders.watchProvidersList.map { it.value }.forEach { watchProvider ->
            val chip = Chip(this)
            // get from string resouce
            chip.text = resources.getIdentifier(watchProvider, "string", packageName).let {
                resources.getString(it)
            }
            chip.isCheckable = true
            watchProviderChipGroup.addView(chip)
            //select it
            chip.isChecked = true
        }

        movieListRv = findViewById(R.id.movie_list_rv)
        selectedMovieListRv = findViewById(R.id.selected_movie_list_rv)
        searchEditText = findViewById(R.id.movie_search_et)
        searchWithFilterButton = findViewById(R.id.search_with_filters_btn)
        clearFiltersImageButton = findViewById(R.id.clear_filters_btn)

        setupSearchBehavior()
        setupFiltersBehavior()
        setupSearchWithFiltersBehavior()
        setupMovieSelectedBehavior()

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

    // Implementing the interaction interface methods
    override fun displayMovieSelectionConfirmationDialog(movieData: MovieDAO) {
        val selectionDialog = MovieSelectionDialogFragment()
        val movieJson: String = Gson().toJson(movieData)
        val args = Bundle()
        args.putString("movie", movieJson)
        selectionDialog.arguments = args
        selectionDialog.show(supportFragmentManager, "MovieSelectionDialogFragment")
    }

    override fun displayMovieDetails(movieData: MovieDAO) {
        val detailsDialog = MovieDescriptionDialog()
        val movieJson: String = Gson().toJson(movieData)
        val args = Bundle()
        args.putString("movie", movieJson)
        detailsDialog.arguments = args
        detailsDialog.show(supportFragmentManager, "MovieDescriptionDialog")
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
            toggleSearchEditText(chipGroup.checkedChipIds.isEmpty())
            setGenresFilter(chipGroup.checkedChipIds.toList().map { it.toString() })
        }
    }

    private fun setupSearchWithFiltersBehavior() {
        searchWithFilterButton.setOnClickListener {
            displayRandomMoviesWithFilters()
        }
        clearFiltersImageButton.setOnClickListener {
            genresChipGroup.clearCheck()
            watchProviderChipGroup.clearCheck()
        }
    }

    /**
     * Enable or disable the search edit text with opacity to indicate the state
     */
    private fun toggleSearchEditText(enable: Boolean) {
        searchEditText.isEnabled = enable
        searchEditText.alpha = if (enable) 1f else 0.38f
    }

    private fun displayRandomMoviesWithFilters() {
        movieViewModel.getRandomFilteredMovies()
    }

    private fun setupMovieSelectedBehavior() {
        supportFragmentManager.setFragmentResultListener(
            "movie_selection_confirm._dialog_selectedMovie", this
        ) { _, bundle ->
            Log.d("MovieSelectionDialogConfirmation", "onFragmentResult")
            // We use a String here, but any type that can be put in a Bundle is supported.
            val selectedMovieJson = bundle.getString("bundleKey")
            val selectedMovie = Gson().fromJson(selectedMovieJson, MovieDAO::class.java)
            movieViewModel.addMovieToSelectedList(selectedMovie)
        }
    }

    private fun getQuery(): String {
        return searchEditText.text.toString()
    }

    private fun setGenresFilter(genres: List<String>) {
        this.movieViewModel.genreFilterIdsLiveData.postValue(genres)
    }

    override fun removeFromSelection(movie: MovieDAO) {
        this.movieViewModel.removeFromSelection(movie)
    }
}
