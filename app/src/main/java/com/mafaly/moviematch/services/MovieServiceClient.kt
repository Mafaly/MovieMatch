package com.mafaly.moviematch.services

import com.mafaly.moviematch.model.MovieCollectionDTO
import io.reactivex.rxjava3.core.Flowable
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieServiceClient {
    @GET("discover/movie?sort_by=popularity.desc")
    fun discoverMovies(
        @Query("with_genres") genres: String,
        @Query("with_watch_providers") watchProviders: String
    ): Flowable<MovieCollectionDTO>

    @GET("search/movie")
    fun searchForMovies(
        @Query("query") query: String,
    ): Flowable<MovieCollectionDTO>
}