package com.mafaly.moviematch.tools

import com.mafaly.moviematch.db.entities.MovieEntity
import com.mafaly.moviematch.model.MovieDTO

fun movieEntityToDTO(movieEntity: MovieEntity): MovieDTO {
    return MovieDTO(
        movieEntity.id,
        movieEntity.movieTitle,
        movieEntity.movieOverview,
        movieEntity.movieGenre ?: emptyList(),
        movieEntity.moviePosterPath,
        movieEntity.movieYear,
    )
}
