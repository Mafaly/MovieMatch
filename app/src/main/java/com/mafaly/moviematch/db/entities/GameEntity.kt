package com.mafaly.moviematch.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_entity")
class GameEntity (
    @PrimaryKey(autoGenerate = true) val uid: Int,
    @ColumnInfo(name = "game_name") val gameName: String,
    @ColumnInfo(name = "game_date") val gameDate: String,
    @ColumnInfo(name = "game_movie_count") val gameMovieCount: Int,
    @ColumnInfo(name = "game_time_per_duel") val gameTimePerDuel: Int,
    @ColumnInfo(name = "game_winner_name") val gameWinnerName: String,
)