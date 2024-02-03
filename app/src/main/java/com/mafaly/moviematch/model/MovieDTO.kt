package com.mafaly.moviematch.model

data class MovieDTO(
    val id: Int,
    val original_title: String,
    val overview: String,
    val poster_path: String,
    val release_date: String,
    val genre_ids: List<Int>
)
