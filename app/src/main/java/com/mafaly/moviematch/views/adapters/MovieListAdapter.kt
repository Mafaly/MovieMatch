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
import com.mafaly.moviematch.repos.MovieGenre
import com.mafaly.moviematchduel.BuildConfig
import com.mafaly.moviematchduel.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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
        val context = holder.itemView.context
        val movie = this.movies[position]
        holder.movieTitleTv.text = movie.title

        // formatting the date
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val outputFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")
        Log.d("MovieListAdapter", "Movie title: ${movie.title} movie id: ${movie.id}")
        if (movie.year != "") {
            val date = LocalDate.parse(movie.year, inputFormatter)
            val formattedDate = date.format(outputFormatter)
            val isReleased = date.isBefore(LocalDate.now())
            holder.movieYearTv.text =
                if (isReleased)
                    String.format(context.resources.getString(R.string.released_on), formattedDate)
                else String.format(context.resources.getString(R.string.releases_on), formattedDate)

        }
        holder.movieGenreTv.text = movie.genre.joinToString(", ")
        holder.itemView.setOnClickListener {
            onClickHandler.displayMovieSelectionConfirmationDialog(movie)
        }
        holder.movieDetailsIb.setOnClickListener {
            onMovieDetailsIconClickHandler.displayMovieDetails(movie)
        }
        if (movie.posterPath != null) {
            Glide.with(holder.itemView).load(BuildConfig.TMDB_IMAGE_URL + movie.posterPath).also {
                Log.d("MovieListAdapter", "Loading image for movie ${movie.title}")
            }.into(holder.moviePosterIv)
        }
        if (movie.genre.isNotEmpty()) {
            val genreNames = MovieGenre.getGenreNames(movie.genre, context)
            holder.movieGenreTv.text = genreNames.joinToString(", ")
        }
    }
}

interface OnMovieClickedInMovieSelectionList {
    fun displayMovieSelectionConfirmationDialog(movieData: MovieDTO)
}

interface OnMovieDetailsClicked {
    fun displayMovieDetails(movieData: MovieDTO)
}
