package com.mafaly.moviematch.model

data class MovieDTO(
    val id: Int,
    val title: String,
    val year: Int,
    val genre: List<String>,
    val poster: String,
)
