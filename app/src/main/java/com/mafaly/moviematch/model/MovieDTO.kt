package com.mafaly.moviematch.model

import com.google.gson.annotations.SerializedName

data class MovieDTO(
    val id: Long,
    @SerializedName("original_title")
    val title: String,
    @SerializedName("release_date")
    val year: String,
    @SerializedName("genre_ids")
    val genre: List<Int>,
    @SerializedName("poster_path")
    val posterPath: String?,
    @SerializedName("overview")
    val overview: String,
)
