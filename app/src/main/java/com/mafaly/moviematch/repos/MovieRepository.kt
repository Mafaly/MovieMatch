package com.mafaly.moviematch.repos

import android.util.Log
import com.google.gson.Gson
import com.mafaly.moviematch.model.MovieDAO
import com.mafaly.moviematch.services.MovieServiceClient
import io.reactivex.rxjava3.core.Flowable

class MovieRepository(
    private val movieServiceClient: MovieServiceClient
) {
    fun discoverRandomMovies(genresId: List<String>): Flowable<List<MovieDAO>> {
        val genresIdsString = genresId.joinToString(",")
        return movieServiceClient.discoverMovies(genresIdsString).map { it ->
            Log.d("MovieRepository", Gson().toJson(it))
            it.results.map {
                MovieDAO(
                    it.id,
                    it.original_title,
                    it.release_date,
                    it.genre_ids,
                    it.poster_path,
                    it.overview
                )
            }
        }
    }

    fun searchForMovies(query: String = ""): Flowable<List<MovieDAO>> {
        return movieServiceClient.searchForMovies(query = query).map { it ->
            Log.d("MovieRepository", Gson().toJson(it))
            it.results.map {
                MovieDAO(
                    it.id,
                    it.original_title,
                    it.release_date,
                    it.genre_ids,
                    it.poster_path,
                    it.overview
                )
            }
        }
    }
}
