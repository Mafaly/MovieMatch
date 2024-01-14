package com.example.moviematchduel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.moviematchduel.databinding.DialogMovieDescriptionBinding

class MovieDescriptionDialog : DialogFragment() {

    private lateinit var binding: DialogMovieDescriptionBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogMovieDescriptionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val movieTitle = arguments?.getString("movieTitle") ?: "No Title"
        val movieDescription = arguments?.getString("movieDescription") ?: "No Description"

        binding.movieDescriptionTitle.text = movieTitle
        binding.movieDescription.text = movieDescription
    }

    companion object {
        fun newInstance(movieTitle: String, movieDescription: String): MovieDescriptionDialog {
            return MovieDescriptionDialog().apply {
                arguments = Bundle().apply {
                    putString("movieTitle", movieTitle)
                    putString("movieDescription", movieDescription)
                }
            }
        }
    }
}






