package com.mafaly.moviematch.views

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.mafaly.moviematch.db.AppDatabase
import com.mafaly.moviematch.db.entities.GameEntity
import com.mafaly.moviematchduel.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Intent(this, MovieSelection::class.java).also {
            startActivity(it)
        }

        val appDatabase = AppDatabase.getInstance(applicationContext)

        // Test
        lifecycleScope.launch(Dispatchers.IO) {
            val game = GameEntity(0, "Partie numéro 1", "2024-01-23", 6, "La La Land")
            appDatabase.gameDao().insertNewGame(game)
        }
    }
}