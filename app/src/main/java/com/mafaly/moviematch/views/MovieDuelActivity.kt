package com.mafaly.moviematch.views

import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.mafaly.moviematch.db.entities.MovieEntity
import com.mafaly.moviematch.game.GameManager
import com.mafaly.moviematch.services.DuelService
import com.mafaly.moviematch.services.MovieService
import com.mafaly.moviematchduel.R
import kotlinx.coroutines.launch
import kotlin.properties.Delegates
import kotlin.random.Random

class MovieDuelActivity : AppCompatActivity() {

    private var duelId by Delegates.notNull<Long>()
    private lateinit var firstDuelComponent: DuelComponent
    private lateinit var secondDuelComponent: DuelComponent
    private lateinit var randomChoiceButton: Button
    private lateinit var duelNumberTextView: TextView
    private lateinit var timeLeftTextView: TextView
    private lateinit var countdownTimer: CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_duel)

        duelId = intent.getLongExtra("duelId", (-1).toLong())
        if (duelId == (-1).toLong()) {
            Toast.makeText(this, "Erreur: DuelId manquant", Toast.LENGTH_SHORT).show()
            finish()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showExitConfirmationDialog()
            }
        })

        lifecycleScope.launch {
            val duel = DuelService.getDuel(this@MovieDuelActivity, duelId)

            val movie1 =
                duel.duelMovie1Id.let { MovieService.getMovieById(this@MovieDuelActivity, it) }
            val movie2 =
                duel.duelMovie2Id?.let { MovieService.getMovieById(this@MovieDuelActivity, it) }

            firstDuelComponent = findViewById(R.id.first_concurrent_component)
            secondDuelComponent = findViewById(R.id.second_concurrent_component)
            timeLeftTextView = findViewById(R.id.time_left_tv)
            randomChoiceButton = findViewById(R.id.random_choice_button)
            duelNumberTextView = findViewById(R.id.duel_number_tv)

            duelNumberTextView.text = getString(R.string.turn_number, duel.duelTurnNumber)

            if (movie1 != null && movie2 != null) {
                firstDuelComponent.setMovieDetails(movie1.movieTitle, movie1.moviePosterPath)
                secondDuelComponent.setMovieDetails(movie2.movieTitle, movie2.moviePosterPath)

                firstDuelComponent.choiceButton.setOnClickListener {
                    handleChoiceButtonClick(movie1)
                }

                secondDuelComponent.choiceButton.setOnClickListener {
                    handleChoiceButtonClick(movie2)
                }

                randomChoiceButton.setOnClickListener {
                    handleChoiceButtonClick(makeRandomChoice(movie1, movie2))
                }
            }

            // Once all the elements are charged, countDownTime initialization
            if (GameManager.getCurrentGame() != null) {
                val timePerDuel = GameManager.getCurrentGame()!!.gameTimePerDuel.times(1000).toLong()
                countdownTimer = object : CountDownTimer(timePerDuel, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        val secondsRemaining = millisUntilFinished / 1000
                        val countdownText = getString(R.string.countdown_timer_text, secondsRemaining)
                        timeLeftTextView.text = countdownText
                    }

                    override fun onFinish() {
                        timeLeftTextView.text = getString(R.string.times_up)
                        if (movie1 != null && movie2 != null) {
                            handleChoiceButtonClick(makeRandomChoice(movie1, movie2))
                        }
                    }
                }.start()
            }
        }
    }

    private fun showExitConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.confirmation))
            .setMessage(getString(R.string.confirm_quit_game))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                finish()
                GameManager.cancelCurrentGame()
            }
            .setNegativeButton(getString(R.string.no), null)
            .show()
    }

    private fun handleChoiceButtonClick(selectedMovie: MovieEntity) {
        countdownTimer.cancel()
        finishDuel(selectedMovie.id)
    }

    private fun finishDuel(winnerId: Long) {
        finish()
        GameManager.finishDuel(this, duelId, winnerId)
    }

    private fun makeRandomChoice(movie1: MovieEntity, movie2: MovieEntity): MovieEntity {
        val random = Random.nextBoolean()

        return if (random) {
            movie1
        } else {
            movie2
        }
    }
}