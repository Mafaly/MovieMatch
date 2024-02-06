package com.mafaly.moviematch.db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mafaly.moviematch.db.entities.DuelEntity

@Dao
interface DuelDao {
    @Query("SELECT * FROM duel_entity WHERE duel_game_id = :gameId")
    fun getDuelsForGame(gameId: Int): List<DuelEntity>

    @Query("SELECT * FROM duel_entity WHERE duel_game_id = :gameId and duel_movie_1_id != null and duel_movie_2_id == null")
    fun getIncompleteDuelsForGame(gameId: Int): List<DuelEntity>

    @Query("SELECT * FROM duel_entity WHERE duel_game_id = :gameId and duel_winner_id == null")
    fun getUnfinishedDuelsForGame(gameId: Int): List<DuelEntity>

    @Query("SELECT * FROM duel_entity WHERE id = :duelId")
    fun getDuel(duelId: Int): DuelEntity

    @Update
    fun updateDuel(duel: DuelEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertDuel(duel: DuelEntity)

    @Query("DELETE FROM duel_entity WHERE duel_game_id = :gameId")
    fun deleteDuelsForGame(gameId: Int)
}