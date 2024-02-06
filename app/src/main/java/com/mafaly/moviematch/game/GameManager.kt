package com.mafaly.moviematch.game

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.mafaly.moviematch.db.AppDatabase
import com.mafaly.moviematch.db.entities.GameEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class GameManager private constructor() {

    private var currentGame: GameEntity? = null

    fun startNewGame(gameName: String, gameMovieCount: Int, gameTimePerDuel: Int) {
        val gameDate = LocalDate.now()
        val formattedDate = gameDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        currentGame = GameEntity(0, gameName, formattedDate, gameMovieCount, gameTimePerDuel, null)
    }

    fun cancelCurrentGame() {
        currentGame = null
    }

    fun finishCurrentGame(context: Context, lifecycleOwner: LifecycleOwner, gameEntity: GameEntity) {
        val appDatabase = AppDatabase.getInstance(context)

        lifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            appDatabase.gameDao().insertNewGame(gameEntity)
        }
        currentGame = null
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
    fun getCurrentGame(): GameEntity? {
        return currentGame
    }

    companion object {
        @Volatile
        private var INSTANCE: GameManager? = null

        fun getInstance(): GameManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: GameManager().also { INSTANCE = it }
            }
        }
    }
}