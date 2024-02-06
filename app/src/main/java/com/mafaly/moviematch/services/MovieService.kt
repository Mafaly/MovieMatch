package com.mafaly.moviematch.services

import android.content.Context
import com.mafaly.moviematch.db.AppDatabase
import com.mafaly.moviematch.db.entities.GameEntity
import com.mafaly.moviematch.db.entities.MovieEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class MovieService {

    companion object {
        suspend fun getMovieById(context: Context, movieId: Int): MovieEntity? {
            return withContext(Dispatchers.IO) {
                val appDatabase = AppDatabase.getInstance(context)
                val deferredDuel = async { appDatabase.movieDao().getMovieById(movieId) }
                deferredDuel.await()
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