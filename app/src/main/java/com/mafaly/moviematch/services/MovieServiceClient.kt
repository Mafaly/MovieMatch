package com.mafaly.moviematch.services

import com.mafaly.moviematch.model.MovieCollectionDTO
import io.reactivex.rxjava3.core.Flowable
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieServiceClient {
    @GET("movie/popular")
    fun getMostPopularMovies(): Flowable<MovieCollectionDTO>

    @GET("movie/upcoming")
    fun getUpcomingMovies(): Flowable<MovieCollectionDTO>

    @GET("search/movie")
    fun searchForMovies(
        @Query("query") query: String,
    ): Flowable<MovieCollectionDTO>
}