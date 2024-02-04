package com.mafaly.moviematch.model

data class MovieDAO(
    val id: Int,
    val title: String,
    val year: String,
    val genre: List<Int>,
    val posterPath: String?,
    val overview: String,
)
