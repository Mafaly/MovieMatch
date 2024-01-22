package com.mafaly.moviematch.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mafaly.moviematch.db.daos.GameDao
import com.mafaly.moviematch.db.entities.GameEntity

@Database(
    entities = [GameEntity::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao
}