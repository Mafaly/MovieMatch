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

    private lateinit var buttonStart: Button
    private lateinit var spinnerNumberOfFilms: Spinner
    private lateinit var editTextTimePerDuel: EditText
    private lateinit var editTextGameName: EditText
    private lateinit var buttonHistory: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_match)

        buttonStart = findViewById(R.id.buttonStart)
        spinnerNumberOfFilms = findViewById(R.id.spinnerNumberOfFilms)
        editTextTimePerDuel = findViewById(R.id.editTextTimePerDuel)
        editTextGameName = findViewById(R.id.editTextGameName)
        buttonHistory=findViewById(R.id.buttonHistory)

        setupSpinner()

        buttonStart.setOnClickListener {
            startGame()
        }
        buttonHistory.setOnClickListener {
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
            spinnerNumberOfFilms.adapter = adapter
        }
    }

    private fun startGame() {
        val gameName = editTextGameName.text.toString().trim().ifEmpty { "Game Number 1" }
        val numberOfFilms = spinnerNumberOfFilms.selectedItem.toString().toInt()
        val timePerDuel = editTextTimePerDuel.text.toString().toIntOrNull() ?: 30

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
