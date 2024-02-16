package com.mafaly.moviematch.tools

import kotlinx.coroutines.flow.Flow

interface ConnectivityObserver {
    fun observe(): Flow<Status>

    fun isOnline(): Boolean

    enum class Status {
        AVAILABLE, UNAVAILABLE, LOSING, LOST
    }
}