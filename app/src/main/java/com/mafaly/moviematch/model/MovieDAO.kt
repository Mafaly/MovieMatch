package com.mafaly.moviematch.model

data class MovieDAO(
    val id: Int,
    val title: String,
    val year: String,
    val genre: List<String>,
    val posterPath: String?,
)
