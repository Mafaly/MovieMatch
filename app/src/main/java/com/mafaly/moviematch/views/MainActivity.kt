package com.mafaly.moviematch.views

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Intent(this, MovieMatchActivity::class.java).also {
            startActivity(it)
        }
    }
}