package com.example.myapplication

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager

class MovieMatchActivity : AppCompatActivity() {

    private lateinit var buttonStart: Button
    private lateinit var spinnerNumberOfFilms: Spinner
    private lateinit var editTextTimePerDuel: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_match)

        buttonStart = findViewById(R.id.buttonStart)
        spinnerNumberOfFilms = findViewById(R.id.spinnerNumberOfFilms)
        editTextTimePerDuel = findViewById(R.id.editTextTimePerDuel)

        setupSpinner()

        buttonStart.setOnClickListener {
            showMovieDetailsDialog()
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



    private fun showMovieDetailsDialog() {
        val numberOfFilms = spinnerNumberOfFilms.selectedItem.toString().toInt()
        val timePerDuel = editTextTimePerDuel.text.toString().toIntOrNull() ?: 30



        val dialog = MovieSelectionDialogFragment()

        // Show the dialog
        val fragmentManager: FragmentManager = supportFragmentManager
        dialog.show(fragmentManager, "MovieSelectionActivity")
    }
}


