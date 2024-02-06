package com.mafaly.moviematch.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "duel_entity",
    foreignKeys = [
        ForeignKey(
            entity = GameEntity::class,
            parentColumns = ["id"],
            childColumns = ["duel_game_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MovieEntity::class,
            parentColumns = ["id"],
            childColumns = ["duel_movie_1_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MovieEntity::class,
            parentColumns = ["id"],
            childColumns = ["duel_movie_2_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MovieEntity::class,
            parentColumns = ["id"],
            childColumns = ["duel_winner_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
class DuelEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "duel_game_id") val duelGameId: Int,
    @ColumnInfo(name = "duel_movie_1_id") val duelMovie1Id: Int,
    @ColumnInfo(name = "duel_movie_2_id") val duelMovie2Id: Int?,
    @ColumnInfo(name = "duel_turn_number") val duelTurnNumber: Int,
    @ColumnInfo(name = "duel_winner_id") var duelWinnerId: Int?
)