package com.mafaly.moviematch.services

import android.content.Context
import com.mafaly.moviematch.db.AppDatabase
import com.mafaly.moviematch.db.entities.GameEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GameService {

    companion object {

        suspend fun getLastGame(context: Context): GameEntity? {
            return withContext(Dispatchers.IO) {
                val appDatabase = AppDatabase.getInstance(context)
                val deferredGame = async { appDatabase.gameDao().getLastGame() }
                deferredGame.await()
            }
        }

        fun insertNewGame(context: Context, gameEntity: GameEntity) {
            CoroutineScope(Dispatchers.IO).launch {
                val appDatabase = AppDatabase.getInstance(context)
                appDatabase.gameDao().insertNewGame(gameEntity)
            }
        }

        fun getAllGames(context: Context, callback: (List<GameEntity>?) -> Unit) {
            val appDatabase = AppDatabase.getInstance(context)
            CoroutineScope(Dispatchers.IO).launch {
                val list = appDatabase.gameDao().getAllGames()
                withContext(Dispatchers.Main) {
                    callback(list)
                }
            }
        }

        fun updateGame(context: Context, gameEntity: GameEntity) {
            CoroutineScope(Dispatchers.IO).launch {
                val appDatabase = AppDatabase.getInstance(context)
                appDatabase.gameDao().updateGame(gameEntity)
            }
        }
    }
}