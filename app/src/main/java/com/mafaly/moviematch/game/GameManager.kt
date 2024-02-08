package com.mafaly.moviematch.game

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.mafaly.moviematch.db.AppDatabase
import com.mafaly.moviematch.db.entities.DuelEntity
import com.mafaly.moviematch.db.entities.GameEntity
import com.mafaly.moviematch.services.DuelService
import com.mafaly.moviematch.services.GameService
import com.mafaly.moviematch.services.MovieService
import com.mafaly.moviematch.views.MovieDuelActivity
import com.mafaly.moviematch.views.MovieWinnerActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    fun getCurrentGame(): GameEntity? {
        return currentGame
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

    fun finishDuel(context: Context, lifecycleOwner: LifecycleOwner, duelId: Long, winnerId: Long) {
        lifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val duel = DuelService.getDuel(context, duelId)

            duel.duelWinnerId = winnerId
            DuelService.updateDuel(context, duel)

            handleGameStep(context, lifecycleOwner)
        }
    }

    fun handleGameStep(context: Context, lifecycleOwner: LifecycleOwner) {

        lifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            if (currentGame != null) {
                if (currentGame!!.gameWinnerId != null) {
                    val movieWinnerIntent = Intent(context, MovieWinnerActivity::class.java)
                    movieWinnerIntent.putExtra("movieId", currentGame!!.gameWinnerId)
                    context.startActivity(movieWinnerIntent)

                    Log.d("WINNER", "gameWinnerId : $currentGame!!.gameWinnerId")

                    return@launch
                }

                var duels = DuelService.getDuelsForGame(context, currentGame!!.id)

                // game does not have duels programmed
                if (duels.isEmpty()) {
                    generateFirstRound(context)
                    duels = DuelService.getDuelsForGame(context, currentGame!!.id)
                }

                // game has duels programmed so next duel is launched
                // get the last round not completed
                val lastRoundDuels = filterLastRoundDuels(duels)

                if (isRoundFinished(lastRoundDuels)) {
                    // the round is finished so let's generate the next round
                    generateNewRound(context, lifecycleOwner, lastRoundDuels)
                } else {
                    // the round is not finished so let's start the next duel
                    val duelIdToDisplay = lastRoundDuels.firstOrNull { it.duelWinnerId == null }?.id

                    val movieDuelIntent = Intent(context, MovieDuelActivity::class.java)
                    movieDuelIntent.putExtra("duelId", duelIdToDisplay)
                    context.startActivity(movieDuelIntent)
                }
            }
        }
    }

    private suspend fun generateFirstRound(context: Context) {
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

    private suspend fun generateNewRound(context: Context, lifecycleOwner: LifecycleOwner, duels: List<DuelEntity>) {
        val currentGame = currentGame ?: return

        if (duels.size == 1) {
            val movieWinnerId = duels.first().duelWinnerId
            currentGame.gameWinnerId = movieWinnerId
            GameService.updateGame(context, currentGame)
            // launch winner layout
            Log.d("WINNER", "movieWinnerId : $movieWinnerId")
        } else if (duels.size >= 2) {
            var duelIndex = 0
            while (duelIndex < duels.size) {
                val firstDuel = duels[duelIndex]
                val newDuel = DuelEntity(
                    0,
                    currentGame.id,
                    firstDuel.duelWinnerId!!,
                    null,
                    firstDuel.duelTurnNumber + 1,
                    null
                )
                duelIndex++

                if (duelIndex < duels.size) {
                    val secondDuel = duels[duelIndex]
                    newDuel.duelMovie2Id = secondDuel.duelWinnerId
                    duelIndex++

                    DuelService.insertDuel(context, newDuel)
                } else {
                    newDuel.duelWinnerId = newDuel.duelMovie1Id
                    DuelService.insertDuel(context, newDuel)
                }
            }
        }
        handleGameStep(context, lifecycleOwner)
    }

    private fun filterLastRoundDuels(duels: List<DuelEntity>): List<DuelEntity> {
        val maxTurnNumber = duels.maxByOrNull { it.duelTurnNumber }?.duelTurnNumber
        return duels.filter { it.duelTurnNumber == maxTurnNumber }
    }

    private fun isRoundFinished(duels: List<DuelEntity>): Boolean {
        return duels.all { it.duelWinnerId != null }
    }
}