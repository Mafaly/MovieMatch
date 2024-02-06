package com.mafaly.moviematch.views

import android.os.Bundle
import android.os.CountDownTimer
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.mafaly.moviematch.services.DuelService
import com.mafaly.moviematch.services.MovieService
import com.mafaly.moviematchduel.R
import kotlinx.coroutines.launch

class MovieDuelActivity : AppCompatActivity() {

    private lateinit var firstDuelComponent: DuelComponent
    private lateinit var secondDuelComponent: DuelComponent
    private lateinit var timeLeftTextView: TextView
    private lateinit var countdownTimer: CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_duel)

        val duelId = intent.getIntExtra("duelId", -1)
        if (duelId == -1) {
            Toast.makeText(this, "Erreur: DuelId manquant", Toast.LENGTH_SHORT).show()
            finish()
        }

        lifecycleScope.launch {
            val duel = DuelService.getDuel(this@MovieDuelActivity, duelId)
            if (duel == null) {
                Toast.makeText(this@MovieDuelActivity, "Erreur: Duel non existant en BDD", Toast.LENGTH_SHORT).show()
                finish()
            }

            val movie1 = duel?.duelMovie1Id?.let { MovieService.getMovieById(this@MovieDuelActivity, it) }
            val movie2 = duel?.duelMovie2Id?.let { MovieService.getMovieById(this@MovieDuelActivity, it) }

            firstDuelComponent = findViewById(R.id.first_concurrent_component)
            secondDuelComponent = findViewById(R.id.second_concurrent_component)
            timeLeftTextView = findViewById(R.id.time_left_tv)

            if (movie1 != null && movie2 != null) {
                firstDuelComponent.setMovieDetails(movie1.movieTitle, movie1.moviePosterPath)
                secondDuelComponent.setMovieDetails(movie2.movieTitle, movie2.moviePosterPath)
            }

            // Once all the elements are charged, countDownTime initialization
            countdownTimer = object : CountDownTimer(30000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    val secondsRemaining = millisUntilFinished / 1000
                    val countdownText = getString(R.string.countdown_timer_text, secondsRemaining)
                    timeLeftTextView.text = countdownText
                }

                override fun onFinish() {
                    timeLeftTextView.text = "Temps écoulé"
                }
            }.start()
        }
    }
}