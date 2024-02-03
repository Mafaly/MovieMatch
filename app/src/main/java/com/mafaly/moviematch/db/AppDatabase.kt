package com.mafaly.moviematch.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mafaly.moviematch.db.daos.DuelDao
import com.mafaly.moviematch.db.daos.GameDao
import com.mafaly.moviematch.db.daos.GameMoviesDao
import com.mafaly.moviematch.db.daos.MovieDao
import com.mafaly.moviematch.db.entities.DuelEntity
import com.mafaly.moviematch.db.entities.GameEntity
import com.mafaly.moviematch.db.entities.GameMoviesEntity
import com.mafaly.moviematch.db.entities.MovieEntity

@Database(
    entities = [
        GameEntity::class,
        MovieEntity::class,
        GameMoviesEntity::class,
        DuelEntity::class,
    ],
    version = 1
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao
    abstract fun movieDao(): MovieDao
    abstract fun GameMoviesDao(): GameMoviesDao
    abstract fun duelDao(): DuelDao


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
