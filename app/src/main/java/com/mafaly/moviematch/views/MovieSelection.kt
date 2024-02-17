package com.mafaly.moviematch.views

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.gson.Gson
import com.mafaly.moviematch.di.injectModuleDependencies
import com.mafaly.moviematch.di.parseConfigurationAndAddItToInjectionModules
import com.mafaly.moviematch.game.GameManager
import com.mafaly.moviematch.model.MovieDTO
import com.mafaly.moviematch.repo.MovieViewModel
import com.mafaly.moviematch.repos.MovieGenre
import com.mafaly.moviematch.repos.MovieWatchProviders
import com.mafaly.moviematch.tools.ConnectivityObserver
import com.mafaly.moviematch.tools.NetworkConnectivityObserver
import com.mafaly.moviematch.views.adapters.MovieListAdapter
import com.mafaly.moviematch.views.adapters.OnMovieClickedInMovieSelectionList
import com.mafaly.moviematch.views.adapters.OnMovieDetailsClicked
import com.mafaly.moviematch.views.adapters.OnSelectedMovieLongClicked
import com.mafaly.moviematch.views.adapters.OnSelectedMovieSimpleClicked
import com.mafaly.moviematch.views.adapters.OnSelectionConfirmation
import com.mafaly.moviematch.views.adapters.SelectedMovieListAdapter
import com.mafaly.moviematchduel.R
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Locale

class MovieSelection : AppCompatActivity(), OnMovieClickedInMovieSelectionList,
    OnMovieDetailsClicked, OnSelectedMovieLongClicked, OnSelectedMovieSimpleClicked,
    OnSelectionConfirmation {

    private val movieViewModel: MovieViewModel by viewModel()
    private val connectivityObserver: NetworkConnectivityObserver by inject()

    private lateinit var movieAdapter: MovieListAdapter

    private lateinit var movieListRv: RecyclerView
    private lateinit var selectedMovieListRv: RecyclerView
    private lateinit var genresChipGroup: ChipGroup
    private lateinit var watchProviderChipGroup: ChipGroup
    private lateinit var searchEditText: EditText
    private lateinit var searchWithFilterButton: MaterialButton
    private lateinit var clearFiltersImageButton: MaterialButton
    private lateinit var selectionConfirmationFAB: FloatingActionButton
    private lateinit var progressIndicator: LinearProgressIndicator
    private lateinit var filtersSectionConstraintLayout: ConstraintLayout
    private lateinit var movieListTitleTv: TextView

    private var searchEditTextTextWatcher: TextWatcher? = null

    @SuppressLint("DiscouragedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Dependency injection
        parseConfigurationAndAddItToInjectionModules()
        injectModuleDependencies(this@MovieSelection)

        // Assign the layout to the activity
        setContentView(R.layout.activity_movie_selection)

        // map over the MovieGenre map to get the genre names
        genresChipGroup = findViewById(R.id.genre_cp_grp)
        // map over the MovieGenre map to get the genre names into chips
        MovieGenre.genreList.map { it }.forEach { genre ->
            val chip = Chip(this)
            chip.id = genre.key
            chip.isCheckable = true
            chip.text = resources.getIdentifier(genre.value, "string", packageName).let {
                resources.getString(it)
            }
            genresChipGroup.addView(chip)
        }

        // map over the MovieWatchProviders map to get the genre names
        watchProviderChipGroup = findViewById(R.id.watch_providers_cp_grp)
        // map over the MovieGenre map to get the genre names into chips
        MovieWatchProviders.watchProvidersList.map { it }.forEach { watchProvider ->
            val chip = Chip(this)
            chip.id = watchProvider.key
            chip.isCheckable = true
            chip.text = MovieWatchProviders.getWatchProviderNames(watchProvider.key, this)
            chip.chipIcon = MovieWatchProviders.getWatchProviderIcon(watchProvider.key, this)
            watchProviderChipGroup.addView(chip)
        }

        movieListRv = findViewById(R.id.movie_list_rv)
        selectedMovieListRv = findViewById(R.id.selected_movie_list_rv)
        searchEditText = findViewById(R.id.movie_search_et)
        searchWithFilterButton = findViewById(R.id.search_with_filters_btn)
        clearFiltersImageButton = findViewById(R.id.clear_filters_btn)
        selectionConfirmationFAB = findViewById(R.id.confirm_selection_fab)
        progressIndicator = findViewById(R.id.confirmed_selection_lpi)
        filtersSectionConstraintLayout = findViewById(R.id.filters_section_cl)
        movieListTitleTv = findViewById(R.id.movies_list_title_tv)

        progressIndicator.hide()
        progressIndicator.isIndeterminate = true

        handleInitialConneciivityStatus()
        handleConnectivtyStatus()
        setupFiltersBehavior()
        setupSearchWithFiltersBehavior()
        setupMovieSelectedBehavior()
        setupSelectionConfirmationBehavior()

        movieViewModel.clearSelectedMovies()

        // Observe the movie data from the view model
        this.observeMovieLiveData()
        handleSubmitButtonStatus()

        // Display initial data on initial load
//        displayRandomMoviesWithFilters()
        displayRandomMoviesWithFilters()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
    }

    private fun observeMovieLiveData() {
        movieViewModel.movieLiveData.observe(this@MovieSelection) { movieList ->
            setUpMovieListViews(movieList)
        }
        movieViewModel.selectedMovieLiveData.observe(this@MovieSelection) { movieList ->
            setUpSelectedMovieListViews(movieList)
            handleSubmitButtonStatus()
        }
    }

    private fun setUpMovieListViews(movies: List<MovieDTO>) {
        movieAdapter = MovieListAdapter(movies, this, this)
        movieListRv.layoutManager = LinearLayoutManager(this)
        movieListRv.adapter = movieAdapter

    }

    private fun setUpSelectedMovieListViews(movies: List<MovieDTO>) {
        val selectedMovieAdapter = SelectedMovieListAdapter(movies, this, this)
        selectedMovieListRv.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        selectedMovieListRv.adapter = selectedMovieAdapter

    }

    // Implementing the interaction interface methods
    override fun displayMovieSelectionConfirmationDialog(movieData: MovieDTO) {
        if (movieViewModel.selectedMovieLiveData.value != null && movieViewModel.selectedMovieLiveData.value!!.size >= GameManager.getCurrentGame()!!.gameMoviesCount) {
            Toast.makeText(
                this,
                getString(
                    R.string.movie_selection_error_too_many
                ),
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        val selectionDialog = MovieSelectionDialogFragment()
        val movieJson: String = Gson().toJson(movieData)
        val args = Bundle()
        args.putString("movie", movieJson)
        selectionDialog.arguments = args
        selectionDialog.show(supportFragmentManager, "MovieSelectionDialogFragment")
    }

    override fun displayMovieDetails(movieData: MovieDTO) {
        val detailsDialog = MovieDescriptionDialog()
        val movieJson: String = Gson().toJson(movieData)
        val args = Bundle()
        args.putString("movie", movieJson)
        detailsDialog.arguments = args
        detailsDialog.show(supportFragmentManager, "MovieDescriptionDialog")
    }

    private fun setupSearchBehavior(online: Boolean) {
        if (online) {
            if (searchEditTextTextWatcher != null) searchEditText.removeTextChangedListener(
                searchEditTextTextWatcher
            )
            // Delay the search for movies until the user has stopped typing to prevent too many requests on the Data source
            searchEditTextTextWatcher = object : TextWatcher {
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

                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
            }
            searchEditText.addTextChangedListener(searchEditTextTextWatcher)
        } else {
            if (searchEditTextTextWatcher != null) searchEditText.removeTextChangedListener(
                searchEditTextTextWatcher
            )
            searchEditTextTextWatcher = object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    movieAdapter.filter.filter(getQuery())
                }

                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
            }
            searchEditText.addTextChangedListener(searchEditTextTextWatcher)
        }

    }

    private fun setupFiltersBehavior() {
        genresChipGroup.setOnCheckedStateChangeListener { chipGroup, _ ->
            toggleSearchEditText(chipGroup.checkedChipIds.isEmpty() && watchProviderChipGroup.checkedChipIds.isEmpty())
            setGenresFilter(chipGroup.checkedChipIds.toList().map { it.toString() })
        }
        watchProviderChipGroup.setOnCheckedStateChangeListener { chipGroup, _ ->
            toggleSearchEditText(chipGroup.checkedChipIds.isEmpty() && genresChipGroup.checkedChipIds.isEmpty())
            setWatchProviderFilter(chipGroup.checkedChipIds.toList().map { it.toString() })
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
            val selectedMovieJson = bundle.getString("bundleKey")
            val selectedMovie = Gson().fromJson(selectedMovieJson, MovieDTO::class.java)
            movieViewModel.addMovieToSelectedList(selectedMovie)
        }
    }

    private fun setupSelectionConfirmationBehavior() {
        selectionConfirmationFAB.setOnClickListener {
            askForSelectionConfirmation()
        }
    }

    private fun getQuery(): String {
        return searchEditText.text.toString()
    }

    private fun setGenresFilter(genres: List<String>) {
        this.movieViewModel.genreFilterIdsLiveData.postValue(genres)
    }

    private fun setWatchProviderFilter(watchProviders: List<String>) {
        this.movieViewModel.watchProviderFilterIdsLiveData.postValue(watchProviders)
    }

    override fun removeFromSelection(movie: MovieDTO) {
        this.movieViewModel.removeFromSelection(movie)
    }

    override fun askForSelectionConfirmation() {
        AlertDialog.Builder(this).setTitle(getString(R.string.confirmation))
            .setMessage(getString(R.string.selection_confirmation))
            .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                handleConfirmSelection()
                dialog.dismiss()
            }.setNegativeButton(getString(R.string.no)) { dialog, _ ->
                dialog.dismiss()
            }.show()

    }

    private fun handleConfirmSelection() {
        val selectedMoviesCount = this.movieViewModel.selectedMovieLiveData.value!!.size
        when {
            selectedMoviesCount < (GameManager.getCurrentGame()!!.gameMoviesCount) -> {
                Toast.makeText(
                    this,
                    getString(
                        R.string.movie_selection_confirmation_error_not_enough,
                        GameManager.getCurrentGame()!!.gameMoviesCount.toString(),
                        this.movieViewModel.selectedMovieLiveData.value!!.size.toString()
                    ), Toast.LENGTH_SHORT
                ).show()
            }

            selectedMoviesCount == GameManager.getCurrentGame()!!.gameMoviesCount -> {
                confirmSelection()
            }

            else -> {
                Toast.makeText(
                    this,
                    getString(
                        R.string.movie_selection_error_too_many
                    ), Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun startGame() {
        GameManager.handleGameStep(this, this)
        finish()
    }

    private fun handleSubmitButtonStatus() {
        val selectedMoviesCount = this.movieViewModel.selectedMovieLiveData.value?.size
        selectionConfirmationFAB.isEnabled =
            selectedMoviesCount == GameManager.getCurrentGame()!!.gameMoviesCount
        selectionConfirmationFAB.alpha = if (selectionConfirmationFAB.isEnabled) 1f else 0.38f
    }

    private fun handleInitialConneciivityStatus() {
        if (connectivityObserver.isOnline()) {
            setupOnlineUI()
        } else {
            setupOfflineUI()
        }
    }

    private fun handleConnectivtyStatus() {
        connectivityObserver.observe().onEach { status ->
            when (status) {
                ConnectivityObserver.Status.AVAILABLE -> {
                    setupOnlineUI()
                }

                else -> {
                    setupOfflineUI()
                }
            }
        }.launchIn(lifecycleScope)
    }

    private fun setupOfflineUI() {
        setupSearchBehavior(online = false)
        filtersSectionConstraintLayout.visibility = ConstraintLayout.GONE
        val offlineMessage =
            "${getString(R.string.movies_list)} (${getString(R.string.offline)})"
        movieListTitleTv.text = offlineMessage
        // show a dialog to inform the user that they are offline
        AlertDialog.Builder(this).setTitle(getString(R.string.offline).replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                Locale.getDefault()
            ) else it.toString()
        })
            .setMessage(getString(R.string.offline_message))
            .setPositiveButton(getString(android.R.string.ok)) { dialog, _ ->
                dialog.dismiss()
            }.show()

    }

    private fun setupOnlineUI() {
        setupSearchBehavior(online = true)
        filtersSectionConstraintLayout.visibility = ConstraintLayout.VISIBLE
        val onlineMessage = getString(R.string.movies_list)
        movieListTitleTv.text = onlineMessage

    }

    override fun confirmSelection() {
        progressIndicator.show()
        movieViewModel.endSelectionProcessObservable.observe(this@MovieSelection) { endSelection ->
            if (endSelection) {
                startGame()
            }
        }
        movieViewModel.confirmSelection()
        Toast.makeText(this, getString(R.string.selection_confirmed), Toast.LENGTH_SHORT).show()
    }
}
