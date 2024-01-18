package com.mafaly.moviematch.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.mafaly.moviematchduel.databinding.SelectMovieBinding

class MovieSelectionDialogFragment : DialogFragment() {

    private var _binding: SelectMovieBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = SelectMovieBinding.inflate(inflater, container, false)
        return binding.root
    }
    private fun showMovieDetailsDialog() {

        val movieTitle = "Movie Title"
        val movieDescription = "This is the movie description."


        val dialog = MovieDescriptionDialog.newInstance(movieTitle, movieDescription)


        val fragmentManager: FragmentManager = parentFragmentManager
        dialog.show(fragmentManager, "MovieDescriptionDialog")
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonNo.setOnClickListener {

            dismiss()
        }

        binding.buttonYes.setOnClickListener {
            showMovieDetailsDialog()
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

