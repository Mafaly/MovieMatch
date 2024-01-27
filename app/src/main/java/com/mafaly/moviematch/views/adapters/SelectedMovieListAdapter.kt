package com.mafaly.moviematch.views.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mafaly.moviematch.model.MovieDAO
import com.mafaly.moviematchduel.BuildConfig
import com.mafaly.moviematchduel.R

class SelectedMovieListAdapter(
    private val movies: List<MovieDAO>,
    private val onSelectedMovieLongClicked: OnSelectedMovieLongClicked,
    private val onSelectedMovieSimpleClicked: OnSelectedMovieSimpleClicked
) :
    RecyclerView.Adapter<SelectedMovieListAdapter.MovieCellViewHolder>() {
    inner class MovieCellViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var movieTitleTv: TextView
        var moviePosterIv: ImageView

        init {
            movieTitleTv = itemView.findViewById(R.id.movie_title_tv)
            moviePosterIv = itemView.findViewById(R.id.movie_poster_iv)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieCellViewHolder {
        val movieView = LayoutInflater.from(parent.context)
            .inflate(R.layout.mini_movie_cell_layout, parent, false)
        return MovieCellViewHolder(movieView)
    }

    override fun getItemCount(): Int {
        return movies.size
    }

    override fun onBindViewHolder(holder: MovieCellViewHolder, position: Int) {
        val movie = this.movies[position]
        holder.movieTitleTv.text = movie.title
        holder.itemView.setOnLongClickListener {
            onSelectedMovieLongClicked.displayMovieDetails(movie)
            return@setOnLongClickListener true
        }
        holder.itemView.setOnClickListener {
            onSelectedMovieSimpleClicked.removeFromSelection(movie)
        }
        if (movie.posterPath != null) {
            Glide.with(holder.itemView).load(BuildConfig.TMDB_IMAGE_URL + movie.posterPath).also {
                Log.d("MovieListAdapter", "Loading image for movie ${movie.title}")
            }.into(holder.moviePosterIv)
        }
    }
}

interface OnSelectedMovieLongClicked {
    fun displayMovieDetails(movieData: MovieDAO)
}

interface OnSelectedMovieSimpleClicked {
    fun removeFromSelection(movie: MovieDAO)
}
