package com.mafaly.moviematch.di.modules

import com.mafaly.moviematch.repo.MovieViewModel
import com.mafaly.moviematch.repos.MovieRepository
import com.mafaly.moviematch.services.MovieServiceClient
import com.mafaly.moviematch.tools.NetworkConnectivityObserver
import org.koin.core.qualifier.named
import org.koin.dsl.module


internal val coreModules = module {
    // Inject a singleton for the user repo
    single { MovieRepository(get(), get()) }

    // Inject user view model
    single { MovieViewModel(get(), get()) }

    single { NetworkConnectivityObserver(get()) }


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