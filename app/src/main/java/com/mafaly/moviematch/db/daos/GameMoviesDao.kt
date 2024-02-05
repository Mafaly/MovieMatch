package com.mafaly.moviematch.db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mafaly.moviematch.db.entities.GameMoviesEntity

@Dao
interface GameMoviesDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertGameMovies(gameMovies: GameMoviesEntity)

    @Query("SELECT * FROM game_movies WHERE game_id = :gameId")
    fun getMoviesForGame(gameId: Long): List<GameMoviesEntity>

    @Query("SELECT * FROM game_movies WHERE movie_id = :movieId")
    fun getGamesForMovie(movieId: Long): List<GameMoviesEntity>

    @Query("DELETE FROM game_movies WHERE game_id = :gameId AND movie_id = :movieId")
    fun removeGameMovieAssociation(gameId: Long, movieId: Long)
}
