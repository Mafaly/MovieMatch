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

    @Insert
    fun insertNewGame(game: GameEntity)

    @Delete
    fun deleteGame(game: GameEntity)
}