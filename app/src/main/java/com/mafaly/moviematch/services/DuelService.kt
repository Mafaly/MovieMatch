package com.mafaly.moviematch.services

import android.content.Context
import com.mafaly.moviematch.db.AppDatabase
import com.mafaly.moviematch.db.entities.DuelEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class DuelService {

    companion object {
        suspend fun getDuel(context: Context, duelId: Long): DuelEntity {
            return withContext(Dispatchers.IO) {
                val appDatabase = AppDatabase.getInstance(context)
                val deferredDuel = async { appDatabase.duelDao().getDuel(duelId) }
                deferredDuel.await()
            }
        }

        suspend fun getDuelsForGame(context: Context, gameId: Long): List<DuelEntity> {
            return withContext(Dispatchers.IO) {
                val appDatabase = AppDatabase.getInstance(context)
                val deferredDuels = async { appDatabase.duelDao().getDuelsForGame(gameId) }
                deferredDuels.await()
            }
        }

        suspend fun getIncompleteDuelsForGame(context: Context, gameId: Long): List<DuelEntity> {
            return withContext(Dispatchers.IO) {
                val appDatabase = AppDatabase.getInstance(context)
                val deferredDuels = async { appDatabase.duelDao().getIncompleteDuelsForGame(gameId) }
                deferredDuels.await()
            }
        }

        suspend fun insertDuel(context: Context, duelEntity: DuelEntity) {
            withContext(Dispatchers.IO) {
                val appDatabase = AppDatabase.getInstance(context)
                appDatabase.duelDao().insertDuel(duelEntity)
            }
        }

        suspend fun updateDuel(context: Context, duelEntity: DuelEntity) {
            withContext(Dispatchers.IO) {
                val appDatabase = AppDatabase.getInstance(context)
                appDatabase.duelDao().updateDuel(duelEntity)
            }
        }
    }
}