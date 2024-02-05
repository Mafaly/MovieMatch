package com.mafaly.moviematch.db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mafaly.moviematch.db.entities.MovieEntity

@Dao
interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertMovie(movie: MovieEntity): Long

    @Query("SELECT * FROM movie_entity")
    fun getAllMovies(): List<MovieEntity>

    @Query("SELECT * FROM movie_entity WHERE id = :movieId")
    fun getMovieById(movieId: Long): MovieEntity?

    @Query("DELETE FROM movie_entity")
    fun deleteAllMovies()

    @Query("DELETE FROM movie_entity WHERE id = :movieId")
    fun deleteMovieById(movieId: Long)
}
