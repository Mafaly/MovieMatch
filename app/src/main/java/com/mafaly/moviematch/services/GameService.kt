package com.mafaly.moviematch.services

import android.content.Context
import com.mafaly.moviematch.db.AppDatabase
import com.mafaly.moviematch.db.entities.DuelEntity
import com.mafaly.moviematch.db.entities.GameEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class GameService {

    companion object {
        suspend fun getGameById(context: Context, gameId: Long): GameEntity {
            return withContext(Dispatchers.IO) {
                val appDatabase = AppDatabase.getInstance(context)
                val deferredGame = async { appDatabase.gameDao().getGameById(gameId) }
                deferredGame.await()
            }
        }

        suspend fun getLastGame(context: Context): GameEntity? {
            return withContext(Dispatchers.IO) {
                val appDatabase = AppDatabase.getInstance(context)
                val deferredGame = async { appDatabase.gameDao().getLastGame() }
                deferredGame.await()
            }
        }

        suspend fun getAllGames(context: Context): List<GameEntity> {
            return withContext(Dispatchers.IO) {
                val appDatabase = AppDatabase.getInstance(context)
                val deferredGames = async { appDatabase.gameDao().getAllGames() }
                deferredGames.await()
            }
        }

        suspend fun insertNewGame(context: Context, gameEntity: GameEntity) {
            withContext(Dispatchers.IO) {
                val appDatabase = AppDatabase.getInstance(context)
                appDatabase.gameDao().insertNewGame(gameEntity)
            }
        }

        suspend fun updateGame(context: Context, gameEntity: GameEntity) {
            withContext(Dispatchers.IO) {
                val appDatabase = AppDatabase.getInstance(context)
                appDatabase.gameDao().updateGame(gameEntity)
            }
        }
    }
}