package com.mafaly.moviematch.di

import android.content.Context
import com.mafaly.moviematch.di.modules.TMDBJsonConf
import com.mafaly.moviematch.di.modules.coreModules
import com.mafaly.moviematch.di.modules.remoteModule
import com.mafaly.moviematch.di.modules.roomDbModules
import com.mafaly.moviematchduel.BuildConfig
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.error.KoinAppAlreadyStartedException
import org.koin.dsl.module


fun injectModuleDependencies(context: Context) {
    try {
        startKoin {
            androidContext(context)
            modules(modules)
        }
    } catch (alreadyStart: KoinAppAlreadyStartedException) {
        loadKoinModules(modules)
    }
}

fun parseConfigurationAndAddItToInjectionModules() {
    val fakeApiConf =
        TMDBJsonConf(baseUrl = BuildConfig.TMDB_API_URL, apiToken = BuildConfig.TMDB_API_TOKEN)
    modules.add(module {
        single { fakeApiConf }
    })
}

private val modules = mutableListOf(coreModules, remoteModule, roomDbModules)