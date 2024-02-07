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
        gameName: String,
        gameMovieCount: Int,
        gameTimePerDuel: Int,
        context: Context,
        lifecycleOwner: LifecycleOwner
    ) {
        val gameDate = LocalDate.now()
        val formattedDate = gameDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        val newGame = GameEntity(0, gameName, formattedDate, gameMovieCount, gameTimePerDuel, null)
        saveNewGame(newGame, context, lifecycleOwner)
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
    fun getAllGames(context: Context, callback: (List<GameEntity>?) -> Unit) {
        val appDatabase = AppDatabase.getInstance(context)
        CoroutineScope(Dispatchers.IO).launch {
            val list = appDatabase.gameDao().getAllGames()
            withContext(Dispatchers.Main) {
                callback(list)
            }
        }
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

            if (duel != null) {
                duel.duelWinnerId = winnerId
                DuelService.updateDuel(context, duel)
            }
        }
    }

    fun handleGameStep(context: Context, lifecycleOwner: LifecycleOwner) {

        lifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            //val lastGame = GameService.getLastGame(context)
            val lastGame = GameService.getGameById(context, 1)
            if (lastGame != null) {
                var duels = DuelService.getDuelsForGame(context, lastGame.id)

                // game does not have duels programmed
                if (duels.isEmpty()) {
                    generateDuels(context, lifecycleOwner)
                    duels = DuelService.getDuelsForGame(context, lastGame.id)
                }

                // TODO
                // game has duels programmed so next duel is launched
                // get the last round not completed

                val lastRoundDuels = filterLastRoundDuels(duels)

                // Suppose you have the information needed to start MovieDuelActivity, such as the duelId
                val duelIdToDisplay = lastRoundDuels.firstOrNull()?.id

                val movieDuelIntent = Intent(context, MovieDuelActivity::class.java)
                movieDuelIntent.putExtra("duelId", duelIdToDisplay)
                context.startActivity(movieDuelIntent)
            }
        }
    }

    private fun filterLastRoundDuels(duels: List<DuelEntity>): List<DuelEntity> {
        val maxTurnNumber = duels.maxByOrNull { it.duelTurnNumber }?.duelTurnNumber
        return duels.filter { it.duelTurnNumber == maxTurnNumber }
    }

    private fun generateSampleMovieList(): List<MovieEntity> {
        val movieList = mutableListOf<MovieEntity>()
        movieList.add(
            MovieEntity(
                1,
                "Movie 1",
                "2022",
                listOf(28, 12),
                "path/to/poster1.jpg",
                "hello"
            )
        )
        movieList.add(
            MovieEntity(
                2,
                "Movie 2",
                "2022",
                listOf(28, 12),
                "path/to/poster2.jpg",
                "good morning"
            )
        )
        movieList.add(
            MovieEntity(
                3,
                "Movie 3",
                "2022",
                listOf(28, 12),
                "path/to/poster3.jpg",
                "good afternoon"
            )
        )
        movieList.add(
            MovieEntity(
                4,
                "Movie 4",
                "2022",
                listOf(28, 12),
                "path/to/poster4.jpg",
                "good night"
            )
        )
        return movieList
    }
}