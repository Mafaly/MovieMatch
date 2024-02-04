package com.mafaly.moviematch.repos

import android.annotation.SuppressLint
import android.content.Context

object MovieGenre {
    val genreList = mapOf(
        28 to "@string/genre_action",
        12 to "@string/genre_adventure",
        16 to "@string/genre_animation",
        35 to "@string/genre_comedy",
        80 to "@string/genre_crime",
        99 to "@string/genre_documentary",
        18 to "@string/genre_drama",
        10751 to "@string/genre_family",
        14 to "@string/genre_fantasy",
        36 to "@string/genre_history",
        27 to "@string/genre_horror",
        10402 to "@string/genre_music",
        9648 to "@string/genre_mystery",
        10749 to "@string/genre_romance",
        878 to "@string/genre_science_fiction",
        10770 to "@string/genre_tv_movie",
        53 to "@string/genre_thriller",
        10752 to "@string/genre_war",
        37 to "@string/genre_western"
    )

    /**
     * Returns a list of genre names from the TMDB Api based on the ids provided.
     * @param ids List of genre ids from the TMDB Api.
     * @param context Context to get the resources from.
     */
    @SuppressLint("DiscouragedApi")
    fun getGenreNames(ids: List<Int>, context: Context): List<String> {
        return getGenreResourcesIds(ids).map { resourceId ->
            context.resources.getIdentifier(resourceId, "string", context.packageName).let {
                context.resources.getString(it)
            }
        }
    }

    private fun getGenreResourcesIds(ids: List<Int>): List<String> {
        return ids.map { genreList[it] ?: "" }
    }
}