package com.mafaly.moviematch.game

import android.content.Context
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.mafaly.moviematch.db.AppDatabase
import com.mafaly.moviematch.db.entities.GameEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object GameManager {

    private var currentGame: GameEntity? = null

    fun startNewGame(
        gameName: String,
        gameMovieCount: Int,
        gameTimePerDuel: Int,
        context: Context,
        lifecycleOwner: LifecycleOwner
    ) {
        val gameDate = LocalDate.now()
        val formattedDate = gameDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        val newGame = GameEntity(gameName, formattedDate, gameMovieCount, gameTimePerDuel, null)
        saveNewGame(newGame, context, lifecycleOwner)
    }

    fun cancelCurrentGame() {
        currentGame = null
    }

    fun getCurrentGame(): GameEntity? {
        return currentGame
    }

    private fun saveNewGame(
        game: GameEntity,
        context: Context,
        lifecycleOwner: LifecycleOwner
    ) {
        val appDatabase = AppDatabase.getInstance(context)
        lifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val insertedId = appDatabase.gameDao().insertNewGame(game)
            currentGame = appDatabase.gameDao().getGameById(insertedId)
            Log.d("GameManager", "Game inserted with id: $insertedId")
        }
    }

    private fun getGameById(gameId: Long, context: Context): GameEntity {
        val appDatabase = AppDatabase.getInstance(context)
        return appDatabase.gameDao().getGameById(gameId)
    }

}