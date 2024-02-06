package com.mafaly.moviematch.services

import android.content.Context
import com.mafaly.moviematch.db.AppDatabase
import com.mafaly.moviematch.db.entities.GameEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class GameService {

    companion object {
        suspend fun getGameById(context: Context, gameId: Long): GameEntity {
            return withContext(Dispatchers.IO) {
                val appDatabase = AppDatabase.getInstance(context)
                val deferredDuel = async { appDatabase.gameDao().getGameById(gameId) }
                deferredDuel.await()
            }
        }

        suspend fun getLastGame(context: Context): GameEntity? {
            return withContext(Dispatchers.IO) {
                val appDatabase = AppDatabase.getInstance(context)
                val deferredDuel = async { appDatabase.gameDao().getLastGame() }
                deferredDuel.await()
            }
        }

        suspend fun insertNewGame(context: Context, gameEntity: GameEntity) {
            withContext(Dispatchers.IO) {
                val appDatabase = AppDatabase.getInstance(context)
                appDatabase.gameDao().insertNewGame(gameEntity)
            }
        }
    }
}