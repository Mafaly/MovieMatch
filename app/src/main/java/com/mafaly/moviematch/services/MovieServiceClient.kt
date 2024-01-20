package com.mafaly.moviematch.services

import com.mafaly.moviematch.model.MovieCollectionDTO
import io.reactivex.rxjava3.core.Flowable
import retrofit2.http.GET

interface MovieServiceClient {
    @GET("movie/popular")
    fun getMostPopularMovies(): Flowable<MovieCollectionDTO>

    @GET("movie/upcoming")
    fun getUpcomingMovies(): Flowable<MovieCollectionDTO>

}