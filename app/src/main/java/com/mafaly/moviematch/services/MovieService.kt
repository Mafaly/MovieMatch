package com.mafaly.moviematch.services

import android.content.Context
import com.mafaly.moviematch.db.AppDatabase
import com.mafaly.moviematch.db.entities.MovieEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class MovieService {

    companion object {
        suspend fun getMovieById(context: Context, movieId: Long): MovieEntity? {
            return withContext(Dispatchers.IO) {
                val appDatabase = AppDatabase.getInstance(context)
                val deferredMovie = async { appDatabase.movieDao().getMovieById(movieId) }
                deferredMovie.await()
            }
        }

        suspend fun getMoviesForGame(context: Context, gameId: Long): List<MovieEntity> {
            return withContext(Dispatchers.IO) {
                val appDatabase = AppDatabase.getInstance(context)
                val moviesGame = appDatabase.GameMoviesDao().getMoviesForGame(gameId)
                val moviesIds = moviesGame.map { it.movieId }
                val deferredMovies = async { appDatabase.movieDao().getMoviesByIds(moviesIds) }
                deferredMovies.await()
            }
        }

        suspend fun insertMovie(context: Context, movieEntity: MovieEntity) {
            withContext(Dispatchers.IO) {
                val appDatabase = AppDatabase.getInstance(context)
                appDatabase.movieDao().insertMovie(movieEntity)
            }
        }
    }
}