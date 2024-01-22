package com.mafaly.moviematch.views

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.mafaly.moviematch.db.AppDatabase
import com.mafaly.moviematchduel.R

class MainActivity : AppCompatActivity() {
    companion object {
        lateinit var database: AppDatabase
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        database = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "movie_match_db"
        ).build()

        Intent(this, MovieSelection::class.java).also {
            startActivity(it)
        }
    }
}