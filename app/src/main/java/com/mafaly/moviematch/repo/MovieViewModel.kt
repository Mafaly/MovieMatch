package com.mafaly.moviematch.repo

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mafaly.moviematch.model.MovieDAO
import com.mafaly.moviematch.repos.MovieRepository
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo

class MovieViewModel(private val movieRepository: MovieRepository) : ViewModel() {

    private val disposeBag = CompositeDisposable()

    // Observable list of movies observeb by the viewmodel
    val movieLiveData: MutableLiveData<List<MovieDAO>> = MutableLiveData()

    init {
        this.getMoviesInfos()
    }

    private fun getMoviesInfos() {
        this.getCurrentPopularMovies()
    }

    private fun getCurrentPopularMovies() {
        this.movieRepository.getPopularMovies()
            .subscribe(
                { movies ->
                    this.movieLiveData.postValue(movies)
                },
                { error ->
                    Log.d(
                        "Error in function getCurrentPopularMovies while fetching data from Fake API",
                        error.message ?: "Default message error"
                    )
                })
            .addTo(disposeBag)
    }

    fun getUpcomingMovies() {
        this.movieRepository.getUpcomingMovies()
            .subscribe(
                { movies ->
                    this.movieLiveData.postValue(movies)
                },
                { error ->
                    Log.d(
                        "Error in function getUpcomingMovies while fetching data from Fake API",
                        error.message ?: "Default message error"
                    )
                })
            .addTo(disposeBag)
    }

    fun searchForMovies(query: String) {
        this.movieRepository.searchForMovies(query)
            .subscribe(
                { movies ->
                    this.movieLiveData.postValue(movies)
                },
                { error ->
                    Log.d(
                        "Error in function getUpcomingMovies while fetching data from Fake API",
                        error.message ?: "Default message error"
                    )
                })
            .addTo(disposeBag)
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