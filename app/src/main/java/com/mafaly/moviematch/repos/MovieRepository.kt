package com.mafaly.moviematch.repos

import android.util.Log
import com.google.gson.Gson
import com.mafaly.moviematch.model.MovieDAO
import com.mafaly.moviematch.services.MovieServiceClient
import io.reactivex.rxjava3.core.Flowable

class MovieRepository(
    private val movieServiceClient: MovieServiceClient
) {
    fun getPopularMovies(): Flowable<List<MovieDAO>> {
        return movieServiceClient.getMostPopularMovies().map { it ->
            Log.d("MovieRepository", Gson().toJson(it))
            it.results.map {
                MovieDAO(
                    it.id,
                    it.original_title,
                    it.release_date,
                    listOf("Romance"),
                    it.poster_path,
                )
            }
        }
    }

    fun getUpcomingMovies(): Flowable<List<MovieDAO>> {
        return movieServiceClient.getUpcomingMovies().map { it ->
            Log.d("MovieRepository", Gson().toJson(it))
            it.results.map {
                MovieDAO(
                    it.id,
                    it.original_title,
                    it.release_date,
                    listOf("Romance"),
                    it.poster_path,
                )
            }
        }
    }
}
