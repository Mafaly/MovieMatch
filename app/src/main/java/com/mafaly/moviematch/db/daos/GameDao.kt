package com.mafaly.moviematch.db.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.mafaly.moviematch.db.entities.GameEntity

@Dao
interface GameDao {
    @Query("SELECT * FROM game_entity")
    fun getAllGames(): List<GameEntity>

    @Query("SELECT * FROM game_entity ORDER BY id DESC LIMIT 1")
    fun getLastGame(): GameEntity?

    @Query("SELECT * FROM game_entity WHERE id = :gameId")
    fun getGameById(gameId: Long): GameEntity

    @Insert
    fun insertNewGame(game: GameEntity): Long

    @Delete
    fun deleteGame(game: GameEntity)
}