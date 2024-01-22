package com.mafaly.moviematch.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mafaly.moviematch.db.daos.GameDao
import com.mafaly.moviematch.db.entities.GameEntity

@Database(
    entities = [GameEntity::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao

    companion object {
        // Volatile garantit que la variable est toujours lue à partir de la mémoire principale
        // ainsi que les modifications apportées par un thread sont visibles par tous les autres threads
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            if (INSTANCE != null) {
                return INSTANCE!!
            }

            // Synchronisation du bloc bloc pour éviter que plusieurs threads créent plusieurs instances simultanément
            synchronized(this) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "movie_match_db"
                    ).build()
                }
                return INSTANCE!!
            }
        }
    }
}
