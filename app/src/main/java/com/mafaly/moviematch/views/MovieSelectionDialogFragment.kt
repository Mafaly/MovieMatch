package com.mafaly.moviematch.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.mafaly.moviematch.repo.MovieViewModel
import com.mafaly.moviematchduel.BuildConfig
import com.mafaly.moviematchduel.databinding.SelectMovieBinding
class MovieSelectionDialogFragment() : DialogFragment() {

    private var _binding: SelectMovieBinding? = null
    private val binding get() = _binding!!

    private val movieViewModel: MovieViewModel by activityViewModels()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = SelectMovieBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        movieViewModel.selectedMovie.observe(viewLifecycleOwner, Observer { movieData ->
            binding.textViewMovieTitle.text = movieData.title
            Glide.with(this)
                .load(BuildConfig.TMDB_IMAGE_URL + movieData.posterPath)
                //.placeholder(R.drawable.placeholder) // Replace with your placeholder image
                //.error(R.drawable.error_image) // Replace with your error image
                .into(binding.imageMoviePoster)
        })

        binding.buttonNo.setOnClickListener {
            dismiss()
        }
        binding.buttonYes.setOnClickListener {
           movieViewModel.confirmMovieSelection()
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
