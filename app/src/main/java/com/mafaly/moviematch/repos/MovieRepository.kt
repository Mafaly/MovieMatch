package com.mafaly.moviematch.repos

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.mafaly.moviematch.db.AppDatabase
import com.mafaly.moviematch.db.entities.GameMoviesEntity
import com.mafaly.moviematch.db.entities.MovieEntity
import com.mafaly.moviematch.model.MovieDTO
import com.mafaly.moviematch.services.MovieServiceClient
import io.reactivex.rxjava3.core.Flowable

class MovieRepository(
    private val movieServiceClient: MovieServiceClient, private val context: Context
) {
    fun discoverRandomMovies(
        genresId: List<String>,
        watchProviders: List<String>
    ): Flowable<List<MovieDTO>> {
        val genresIdsString = genresId.joinToString(",")
        val watchProvidersString = watchProviders.joinToString("|")
        return movieServiceClient.discoverMovies(genresIdsString, watchProvidersString).map {
            Log.d("MovieRepository", Gson().toJson(it))
            it.results
        }
    }

    fun searchForMovies(query: String = ""): Flowable<List<MovieDTO>> {
        return movieServiceClient.searchForMovies(query = query).map {
            Log.d("MovieRepository", Gson().toJson(it))
            it.results
        }
    }

    fun cacheMovieList(movies: List<MovieDTO>) {
        movies.forEach {
            Log.d("MovieManager", "Caching movie: ${it.id}")
            cacheMovie(it)
        }
    }

    fun saveGameMovieList(movies: List<MovieDTO>, gameID: Long) {
        movies.forEach { movie ->
            Log.d("MovieManager", "Caching movie: ${movie.id}")
            cacheMovie(movie)
            val appDatabase = AppDatabase.getInstance(context)
            appDatabase.GameMoviesDao().insertGameMovies(GameMoviesEntity(gameID, movie.id))
        }
    }

    fun getAllSavedGames(): List<MovieDTO> {
        val appDatabase = AppDatabase.getInstance(context)
        return appDatabase.movieDao().getAllMovies().map { movieEntity ->
            MovieDTO(
                movieEntity.id,
                movieEntity.movieTitle,
                movieEntity.movieYear,
                movieEntity.movieGenre ?: emptyList(),
                movieEntity.moviePosterPath,
                movieEntity.movieOverview
            )
        }
    }


    private fun cacheMovie(movie: MovieDTO) {
        if (!movieExists(movie)) {
            createGame(movie)
        }
    }

    private fun movieExists(
        movie: MovieDTO
    ): Boolean {
        Log.d("MovieManager", "Checking if movie exists: ${movie.id}")
        val appDatabase = AppDatabase.getInstance(context)
        val movieResult = appDatabase.movieDao().getMovieById(movie.id)
        Log.d("MovieManager", "Movie exists: ${movieResult != null}")
        return movieResult != null
    }

    private fun createGame(movie: MovieDTO) {
        val appDatabase = AppDatabase.getInstance(context)
        val movieToInsert = MovieEntity(
            movie.id, movie.title, movie.year, movie.genre, movie.posterPath, movie.overview
        )
        Log.d("MovieManager", "Inserting movie: $movieToInsert")
        val insertedMovieId = appDatabase.movieDao().insertMovie(movieToInsert)
        Log.d("MovieManager", "Movie inserted with id: $insertedMovieId")
    }

}
