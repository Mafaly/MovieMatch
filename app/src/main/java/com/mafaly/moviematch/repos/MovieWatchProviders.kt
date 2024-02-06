package com.mafaly.moviematch.repos

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.appcompat.content.res.AppCompatResources
import com.mafaly.moviematchduel.R

object MovieWatchProviders {
    val watchProvidersList = mapOf(
        2 to "@string/watch_provider_apple_tv",
        3 to "@string/watch_provider_google_play_movies",
        8 to "@string/watch_provider_netflix",
        9 to "@string/watch_provider_prime_video",
        337 to "@string/watch_provider_disney_plus",
    )

    /**
     * Returns a list of watch provider names from the TMDB Api based on the ids provided.
     * @param ids List of genre ids from the TMDB Api.
     * @param context Context to get the resources from.
     */
    fun getWatchProviderNames(ids: Int, context: Context): String {
        return when (ids) {
            2 -> context.resources.getString(R.string.watch_provider_apple_tv)
            3 -> context.resources.getString(R.string.watch_provider_google_play_movies)
            8 -> context.resources.getString(R.string.watch_provider_netflix)
            9 -> context.resources.getString(R.string.watch_provider_prime_video)
            337 -> context.resources.getString(R.string.watch_provider_disney_plus)
            else -> throw IllegalArgumentException("Invalid watch provider id")
        }
    }

    fun getWatchProviderIcon(watchProviderId: Int, context: Context): Drawable? {
        return when (watchProviderId) {
            2 -> AppCompatResources.getDrawable(context, R.drawable.icon_apple_tv)
            3 -> AppCompatResources.getDrawable(context, R.drawable.icon_google_play)
            8 -> AppCompatResources.getDrawable(context, R.drawable.icon_netflix)
            9 -> AppCompatResources.getDrawable(context, R.drawable.icon_amazon_prime)
            337 -> AppCompatResources.getDrawable(context, R.drawable.icon_disney_plus)
            else -> null
        }
    }
}