package com.mafaly.moviematch.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mafaly.moviematch.model.MovieDAO

@Entity(tableName = "movie_entity")
class MovieEntity(
    @PrimaryKey val id: Long,
    @ColumnInfo(name = "movie_title") val movieTitle: String,
    @ColumnInfo(name = "movie_year") val movieYear: String,
    @ColumnInfo(name = "movie_genre") val movieGenre: List<Int>?,
    @ColumnInfo(name = "movie_poster_path") val moviePosterPath: String?,
    @ColumnInfo(name = "movie_overview") val movieOverview: String,
){
    fun toDAO():MovieDAO{
        val movieGenres = if(movieGenre!==null) movieGenre else listOf()
        return MovieDAO(id, movieTitle, movieYear, movieGenres, moviePosterPath, movieOverview)
    }
}