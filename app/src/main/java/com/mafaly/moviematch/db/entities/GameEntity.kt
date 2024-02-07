package com.mafaly.moviematch.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "game_entity",
    foreignKeys = [
        ForeignKey(
            entity = MovieEntity::class,
            parentColumns = ["id"],
            childColumns = ["game_winner_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
class GameEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "game_name") val gameName: String,
    @ColumnInfo(name = "game_date") val gameDate: String,
    @ColumnInfo(name = "game_movies_count") val gameMoviesCount: Int,
    @ColumnInfo(name = "game_time_per_duel") val gameTimePerDuel: Int,
    @ColumnInfo(name = "game_winner_id") var gameWinnerId: Long?
)