package com.mafaly.moviematch.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.mafaly.moviematch.repo.MovieViewModel
import com.mafaly.moviematchduel.databinding.DialogMovieDescriptionBinding

class MovieDescriptionDialog : DialogFragment() {

    private lateinit var binding: DialogMovieDescriptionBinding
    private val movieViewModel: MovieViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogMovieDescriptionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Observe the LiveData from the ViewModel
        movieViewModel.selectedMovie.observe(viewLifecycleOwner, Observer { movieData ->
            // Use movieData to update UI
            binding.movieDescriptionTitle.text = movieData.title
            binding.releaseDate.text=movieData.year
            //binding.movieDescription.text = movieData.description
        })
    }
}






