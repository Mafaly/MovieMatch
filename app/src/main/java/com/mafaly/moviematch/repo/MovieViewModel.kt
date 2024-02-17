package com.mafaly.moviematch.repo

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mafaly.moviematch.game.GameManager
import com.mafaly.moviematch.model.MovieDTO
import com.mafaly.moviematch.repos.MovieRepository
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MovieViewModel(private val movieRepository: MovieRepository) : ViewModel() {

    private val disposeBag = CompositeDisposable()

    // Observable list of movies observed by the viewModel
    val movieLiveData: MutableLiveData<List<MovieDTO>> = MutableLiveData()
    val selectedMovieLiveData: MutableLiveData<List<MovieDTO>> = MutableLiveData()
    val genreFilterIdsLiveData: MutableLiveData<List<String>> = MutableLiveData()
    val watchProviderFilterIdsLiveData: MutableLiveData<List<String>> = MutableLiveData()
    val endSelectionProcessObservable: MutableLiveData<Boolean> = MutableLiveData(false)

    fun getRandomFilteredMovies() {
        val genres = this.genreFilterIdsLiveData.value?.ifEmpty { emptyList() } ?: emptyList()
        val watchProviders =
            this.watchProviderFilterIdsLiveData.value?.ifEmpty { emptyList() } ?: emptyList()
        this.movieRepository.discoverRandomMovies(genres, watchProviders)
            .subscribe(
                { movies ->
                    this.movieLiveData.postValue(movies)
                }, { error ->
                    Log.d(
                        "Error in function getUpcomingMovies while fetching data from Fake API",
                        error.message ?: "Default message error"
                    )
                }).addTo(disposeBag)
    }

    fun addMovieToSelectedList(movie: MovieDTO) {
        val currentSelectedMovies =
            this.selectedMovieLiveData.value?.toMutableList() ?: mutableListOf()
        if (currentSelectedMovies.find { it.id == movie.id } != null) {
            return
        }
        currentSelectedMovies.add(movie)
        this.selectedMovieLiveData.postValue(currentSelectedMovies)
    }

    fun searchForMovies(query: String) {
        this.movieRepository.searchForMovies(query).subscribe({ movies ->
            this.movieLiveData.postValue(movies)
        }, { error ->
            Log.d(
                "Error in function getUpcomingMovies while fetching data from Fake API",
                error.message ?: "Default message error"
            )
        }).addTo(disposeBag)
    }

    fun removeFromSelection(movie: MovieDTO) {
        val currentSelectedMovies =
            this.selectedMovieLiveData.value?.toMutableList() ?: mutableListOf()
        currentSelectedMovies.remove(movie)
        this.selectedMovieLiveData.postValue(currentSelectedMovies)

    }

    fun confirmSelection() {
        GameManager.getCurrentGame()?.id?.let { gameId ->
            this.selectedMovieLiveData.value?.let { movieList ->
                CoroutineScope(Dispatchers.IO).launch {
                    movieRepository.cacheMovieList(movieList)
                    movieRepository.saveGameMovieList(movieList, gameId)
                    delay(3000)
                    endSelectionProcessObservable.postValue(true)
                }
            }
        }
    }

    fun clearSelectedMovies() {
        selectedMovieLiveData.value?.let {
            val currentSelectedMovies = it.toMutableList()
            currentSelectedMovies.clear()
            selectedMovieLiveData.postValue(currentSelectedMovies)
        }
    }

    // TODO: Implement the following function
//    fun searchForMoviesWithFilters(query: String) {
//        this.movieRepository.discoverMoviesByFilters(query)
//            .subscribe(
//                { movies ->
//                    this.movieLiveData.postValue(movies)
//                },
//                { error ->
//                    Log.d(
//                        "Error in function getUpcomingMovies while fetching data from Fake API",
//                        error.message ?: "Default message error"
//                    )
//                })
//            .addTo(disposeBag)
//    }

}