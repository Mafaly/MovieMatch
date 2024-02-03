package com.mafaly.moviematch.db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mafaly.moviematch.db.entities.DuelEntity

@Dao
interface DuelDao {
    @Query("SELECT * FROM duel_entity WHERE duel_game_id = :gameId")
    fun getDuelsForGame(gameId: Int): List<DuelEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertDuel(duel: DuelEntity)

    @Query("DELETE FROM duel_entity WHERE duel_game_id = :gameId")
    fun deleteDuelsForGame(gameId: Int)
}