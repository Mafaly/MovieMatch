package com.mafaly.moviematch.game

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.mafaly.moviematch.db.entities.DuelEntity
import com.mafaly.moviematch.db.entities.GameEntity
import com.mafaly.moviematch.services.DuelService
import com.mafaly.moviematch.services.GameService
import com.mafaly.moviematch.services.MovieService
import com.mafaly.moviematch.views.MovieDuelActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object GameManager {

    private var currentGame: GameEntity? = null

    fun startNewGame(
        context: Context,
        lifecycleOwner: LifecycleOwner,
        gameName: String,
        gameMovieCount: Int,
        gameTimePerDuel: Int,
    ) {
        val gameDate = LocalDate.now()
        val formattedDate = gameDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        val newGame = GameEntity(0, gameName, formattedDate, gameMovieCount, gameTimePerDuel, null)

        lifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            GameService.insertNewGame(context, newGame)
            currentGame = GameService.getLastGame(context)
        }
    }

    fun cancelCurrentGame() {
        currentGame = null
    }

    fun finishCurrentGame(
        context: Context,
        lifecycleOwner: LifecycleOwner,
        gameEntity: GameEntity
    ) {
        lifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            GameService.insertNewGame(context, gameEntity)
        }
        currentGame = null
    }

    fun getCurrentGame(): GameEntity? {
        return currentGame
    }

    fun finishDuel(context: Context, lifecycleOwner: LifecycleOwner, duelId: Long, winnerId: Long) {
        lifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val duel = DuelService.getDuel(context, duelId)

            duel.duelWinnerId = winnerId
            DuelService.updateDuel(context, duel)
        }
    }

    fun handleGameStep(context: Context, lifecycleOwner: LifecycleOwner) {

        lifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            if (currentGame != null) {
                var duels = DuelService.getDuelsForGame(context, currentGame!!.id)

                // game does not have duels programmed
                if (duels.isEmpty()) {
                    generateDuels(context)
                    duels = DuelService.getDuelsForGame(context, currentGame!!.id)
                }

                // TODO
                // game has duels programmed so next duel is launched
                // get the last round not completed

                val lastRoundDuels = filterLastRoundDuels(duels)

                val duelIdToDisplay = lastRoundDuels.firstOrNull()?.id

                val movieDuelIntent = Intent(context, MovieDuelActivity::class.java)
                movieDuelIntent.putExtra("duelId", duelIdToDisplay)
                context.startActivity(movieDuelIntent)
            }
        }
    }

    private suspend fun generateDuels(context: Context) {
        val currentGame = currentGame ?: return
        val movies = MovieService.getMoviesForGame(context, currentGame.id)

        if (movies.size >= 2) {
            var duelIndex = 0
            while (duelIndex < movies.size - 1) {
                val movie1 = movies[duelIndex]
                val movie2 = movies[duelIndex + 1]
                val duel = DuelEntity(
                    0,
                    currentGame.id,
                    movie1.id,
                    movie2.id,
                    1,
                    null
                )

                DuelService.insertDuel(context, duel)
                duelIndex += 2
            }
        }
    }

    private fun filterLastRoundDuels(duels: List<DuelEntity>): List<DuelEntity> {
        val maxTurnNumber = duels.maxByOrNull { it.duelTurnNumber }?.duelTurnNumber
        return duels.filter { it.duelTurnNumber == maxTurnNumber }
    }
}