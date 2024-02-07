package com.mafaly.moviematch.game

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.mafaly.moviematch.db.AppDatabase
import com.mafaly.moviematch.db.entities.DuelEntity
import com.mafaly.moviematch.db.entities.GameEntity
import com.mafaly.moviematch.db.entities.MovieEntity
import com.mafaly.moviematch.services.DuelService
import com.mafaly.moviematch.services.GameService
import com.mafaly.moviematch.services.MovieService
import com.mafaly.moviematch.views.MovieDuelActivity
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

    fun getAllGames(context: Context, callback: (List<GameEntity>?) -> Unit) {
        val appDatabase = AppDatabase.getInstance(context)
        CoroutineScope(Dispatchers.IO).launch {
            val list = appDatabase.gameDao().getAllGames()
            withContext(Dispatchers.Main) {
                callback(list)
            }
        }
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

    private fun generateDuels(context: Context, lifecycleOwner: LifecycleOwner) {
        //TODO get game movies from BDD
        val movies = generateSampleMovieList()
        lifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            for (movie in movies) {
                MovieService.insertMovie(context, movie)
            }

            var moviesLeft = movies.size
            var duelIndex = 0
            while (duelIndex != movies.size) {
                if (moviesLeft >= 2) {
                    val movie1 = movies[duelIndex]
                    val movie2 = movies[duelIndex + 1]
                    val duel = DuelEntity(
                        0,
                        1,
                        movie1.id,
                        movie2.id,
                        1,
                        null
                    )

                    DuelService.insertDuel(context, duel)
                    duelIndex += 2
                    moviesLeft -= 2
                }
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
                    generateNewRound(context, lastRoundDuels)
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

    private suspend fun generateNewRound(context: Context, duels: List<DuelEntity>) {
        if (duels.size == 1) {
            // launch winner layout
        } else if (duels.size >= 2) {
            //TODO generate new round
        }
    }

    private fun filterLastRoundDuels(duels: List<DuelEntity>): List<DuelEntity> {
        val maxTurnNumber = duels.maxByOrNull { it.duelTurnNumber }?.duelTurnNumber
        return duels.filter { it.duelTurnNumber == maxTurnNumber }
    }

    private fun isRoundFinished(duels: List<DuelEntity>): Boolean {
        return duels.all { it.duelWinnerId != null }
    }
}