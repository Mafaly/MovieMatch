package com.mafaly.moviematch.di.modules

import com.mafaly.moviematch.db.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

internal val roomDbModules = module {
    single { AppDatabase.getInstance(androidContext()) }
}