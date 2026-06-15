package com.tradinganalytics.data.repository

import com.tradinganalytics.data.database.dao.EntryDao
import com.tradinganalytics.data.database.entities.EntryEntity
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EntryRepository @Inject constructor(
    private val entryDao: EntryDao
) {

    suspend fun addEntry(entry: EntryEntity): Result<EntryEntity> {
        return try {
            val id = entryDao.insert(entry)
            Result.success(entry.copy(id = id))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getHistory(userId: Long): Flow<List<EntryEntity>> = entryDao.getAllByUser(userId)

    fun getByDateRange(userId: Long, startDate: Date, endDate: Date): Flow<List<EntryEntity>> =
        entryDao.getByDateRange(userId, startDate, endDate)

    fun search(query: String, userId: Long): Flow<List<EntryEntity>> =
        entryDao.searchByNotes(userId, query)

    suspend fun getStats(userId: Long): Result<EntryStats> {
        return try {
            val entries = entryDao.getAllByUser(userId)
            val list = entries.value ?: emptyList()
            val total = list.size
            val wins = list.count { it.result == "WIN" }
            val losses = list.count { it.result == "LOSS" }
            val winRate = if (total > 0) (wins.toDouble() / total) * 100 else 0.0
            val totalProfit = list.filter { it.result == "WIN" }.sumOf { it.amount }
            val totalLoss = list.filter { it.result == "LOSS" }.sumOf { it.amount }
            val netPnL = totalProfit - totalLoss

            Result.success(
                EntryStats(
                    totalEntries = total,
                    wins = wins,
                    losses = losses,
                    winRate = winRate,
                    totalProfit = totalProfit,
                    totalLoss = totalLoss,
                    netPnL = netPnL
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

data class EntryStats(
    val totalEntries: Int,
    val wins: Int,
    val losses: Int,
    val winRate: Double,
    val totalProfit: Double,
    val totalLoss: Double,
    val netPnL: Double
)
