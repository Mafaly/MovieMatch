package com.mafaly.moviematch.di.modules

import com.mafaly.moviematch.repo.MovieViewModel
import com.mafaly.moviematch.repos.MovieRepository
import com.mafaly.moviematch.services.MovieServiceClient
import org.koin.core.qualifier.named
import org.koin.dsl.module


internal val coreModules = module {
    // Inject a singleton for the user repo
    single { MovieRepository(get()) }

    // Inject user view model
    single { MovieViewModel(get()) }


    // Webservices
    single {
        createWebService<MovieServiceClient>(
            get(
                named(TMDBApiRetrofitClient)
            )
        )
    }
}

// Class representing the configuration to parse from the gradle file
data class TMDBJsonConf(
    val baseUrl: String,
    val apiToken: String,
)