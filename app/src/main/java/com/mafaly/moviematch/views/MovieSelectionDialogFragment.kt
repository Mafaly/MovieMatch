package com.mafaly.moviematch.views

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.mafaly.moviematch.model.MovieDAO
import com.mafaly.moviematch.repos.MovieGenre
import com.mafaly.moviematchduel.BuildConfig
import com.mafaly.moviematchduel.databinding.SelectMovieBinding


class MovieSelectionDialogFragment : DialogFragment() {

    private var _binding: SelectMovieBinding? = null
    private val binding get() = _binding!!

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels)
        val height = (resources.displayMetrics.heightPixels * 0.55).toInt()
        dialog?.window?.setLayout(width, height)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = SelectMovieBinding.inflate(inflater, container, false)
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

        binding.movieTitleTv.text = movieData.title
        binding.movieGenreTv.text =  MovieGenre.getGenreNames(movieData.genre, requireContext()).joinToString(", ")

        Glide.with(this).load(BuildConfig.TMDB_IMAGE_URL + movieData.posterPath)
            .into(binding.moviePosterIv)

        binding.noButton.setOnClickListener {
            dismiss()
        }
        binding.yesButton.setOnClickListener {
            setFragmentResult(
                "movie_selection_confirm._dialog_selectedMovie",
                bundleOf("bundleKey" to movieJson)
            )
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
