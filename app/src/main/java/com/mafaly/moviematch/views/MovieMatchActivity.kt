package com.mafaly.moviematch.views

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.mafaly.moviematch.game.GameManager
import com.mafaly.moviematch.views.GameHistoryBottomSheetFragment.Companion.TAG
import com.mafaly.moviematchduel.R

class MovieMatchActivity : AppCompatActivity() {

    private lateinit var startButton: Button
    private lateinit var numberOfFilmsSpinner: Spinner
    private lateinit var timePerDuelEditText: EditText
    private lateinit var gameNameEditText: EditText
    private lateinit var historyButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_match)

        startButton = findViewById(R.id.start_button)
        numberOfFilmsSpinner = findViewById(R.id.number_films_s)
        timePerDuelEditText = findViewById(R.id.time_per_duel_et)
        gameNameEditText = findViewById(R.id.game_name_et)
        historyButton=findViewById(R.id.history_button)

        setupSpinner()

        startButton.setOnClickListener {
            startGame()
        }
        historyButton.setOnClickListener {
                showMovieHistory()
        }

    }

    private fun setupSpinner() {
        ArrayAdapter.createFromResource(
            this,
            R.array.number_of_films_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            numberOfFilmsSpinner.adapter = adapter
        }
    }

    private fun startGame() {
        val gameName = gameNameEditText.text.toString().trim().ifEmpty { "Game Number 1" }
        val numberOfFilms = numberOfFilmsSpinner.selectedItem.toString().toInt()
        val timePerDuel = timePerDuelEditText.text.toString().toIntOrNull() ?: 30

        GameManager.startNewGame(gameName, numberOfFilms, timePerDuel,this,this)

        showMovieSelectionActivity()
    }

    private fun showMovieSelectionActivity() {
        val intent = Intent(this, MovieSelection::class.java)
        startActivity(intent)
    }
    private fun showMovieHistory() {
        GameHistoryBottomSheetFragment().apply {
            isCancelable = true
            show(supportFragmentManager, TAG)
        }
    }
}
