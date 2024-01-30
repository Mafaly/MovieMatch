package com.mafaly.moviematch.views

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.mafaly.moviematch.model.MovieDAO
import com.mafaly.moviematchduel.BuildConfig
import com.mafaly.moviematchduel.databinding.DialogMovieDescriptionBinding

class MovieDescriptionDialog : DialogFragment() {

    private lateinit var binding: DialogMovieDescriptionBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogMovieDescriptionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val movieJson = requireArguments().getString("movie")
        if (movieJson != null) {
            Log.d("MovieSelectionDialog", movieJson)
        }

        val movieData = Gson().fromJson(movieJson, MovieDAO::class.java)
        Log.d("MovieSelectionDialog", movieData.toString())

        binding.movieDescriptionTitle.text = movieData.title
        binding.releaseDate.text = movieData.year
        Glide.with(this)
            .load(BuildConfig.TMDB_IMAGE_URL + movieData.posterPath)
            .into(binding.moviePoster)

        binding.movieOverview.text = movieData.overview

    }
}






