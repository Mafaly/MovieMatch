package com.mafaly.moviematch.views.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mafaly.moviematch.model.MovieDTO
import com.mafaly.moviematchduel.R

class MovieListAdapter(
    private val movies: List<MovieDTO>,
    private val onClickHandler: OnMovieClickedInMovieSelectionList,
    private val onMovieDetailsIconClickHandler: OnMovieDetailsClicked
) :
    RecyclerView.Adapter<MovieListAdapter.MovieCellViewHolder>() {
    inner class MovieCellViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var movieTitleTv: TextView
        var movieYearTv: TextView
        var movieGenreTv: TextView
        var moviePosterIv: ImageView
        var movieDetailsIb: ImageButton

        init {
            movieTitleTv = itemView.findViewById(R.id.movie_title_tv)
            movieYearTv = itemView.findViewById(R.id.movie_year_tv)
            movieGenreTv = itemView.findViewById(R.id.movie_genre_tv)
            moviePosterIv = itemView.findViewById(R.id.movie_poster_iv)
            movieDetailsIb = itemView.findViewById(R.id.movie_details_ib)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieCellViewHolder {
        val movieView = LayoutInflater.from(parent.context)
            .inflate(R.layout.movie_cell_layout, parent, false)
        return MovieCellViewHolder(movieView)
    }

    override fun getItemCount(): Int {
        return movies.size
    }

    override fun onBindViewHolder(holder: MovieCellViewHolder, position: Int) {
        val movie = this.movies[position]
        holder.movieTitleTv.text = movie.title
        holder.movieYearTv.text = movie.year.toString()
        holder.movieGenreTv.text = movie.genre.joinToString(", ")
        holder.itemView.setOnClickListener {
            onClickHandler.displayMovieSelectionConfirmationDialog(movie)
        }
        holder.movieDetailsIb.setOnClickListener {
            onMovieDetailsIconClickHandler.displayMovieDetails(movie)
        }
        Glide
            .with(holder.itemView)
            .load("https://m.media-amazon.com/images/I/81pck0VNp7L._AC_UF1000,1000_QL80_.jpg")
            .also {
                Log.d("MovieListAdapter", "Loading image for movie ${movie.title}")
            }
            .into(holder.moviePosterIv)
    }
}

interface OnMovieClickedInMovieSelectionList {
    fun displayMovieSelectionConfirmationDialog(movieData: MovieDTO)
}

interface OnMovieDetailsClicked {
    fun displayMovieDetails(movieData: MovieDTO)
}
