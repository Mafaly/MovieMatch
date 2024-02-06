package com.mafaly.moviematch.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "game_movies",
    primaryKeys = ["game_id", "movie_id"],
    foreignKeys = [
        ForeignKey(
            entity = GameEntity::class,
            parentColumns = ["id"],
            childColumns = ["game_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MovieEntity::class,
            parentColumns = ["id"],
            childColumns = ["movie_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
class GameMoviesEntity(
    @ColumnInfo(name = "game_id") val gameId: Long,
    @ColumnInfo(name = "movie_id") val movieId: Long
)
