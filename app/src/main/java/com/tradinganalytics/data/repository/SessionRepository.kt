package com.tradinganalytics.data.repository

import com.tradinganalytics.data.database.dao.EntryDao
import com.tradinganalytics.data.database.dao.SessionDao
import com.tradinganalytics.data.database.entities.SessionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionRepository @Inject constructor(
    private val sessionDao: SessionDao,
    private val entryDao: EntryDao
) {

    suspend fun getActiveSession(userId: Long): Result<SessionEntity?> {
        return try {
            Result.success(sessionDao.getActiveSession(userId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getSessionHistory(userId: Long): Flow<List<SessionEntity>> = sessionDao.getAllByUser(userId)

    suspend fun startSession(userId: Long): Result<SessionEntity> {
        return try {
            val activeSession = sessionDao.getActiveSession(userId)
            if (activeSession != null) {
                return Result.failure(IllegalStateException("An active session already exists"))
            }

            val session = SessionEntity(
                userId = userId,
                startTime = Date(),
                status = "ACTIVE"
            )
            val id = sessionDao.insert(session)
            Result.success(session.copy(id = id))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun endSession(userId: Long): Result<SessionEntity> {
        return try {
            val session = sessionDao.getActiveSession(userId)
                ?: return Result.failure(IllegalStateException("No active session found"))

            val entries = entryDao.getByDateRange(
                userId = userId,
                startDate = session.startTime,
                endDate = Date()
            ).first()

            val totalEntries = entries.size
            val wins = entries.count { it.result == "WIN" }
            val profitLoss = entries.sumOf { entry ->
                if (entry.result == "WIN") entry.amount else -entry.amount
            }
            val successRate = if (totalEntries > 0) (wins.toDouble() / totalEntries) * 100 else 0.0

            val updatedSession = session.copy(
                endTime = Date(),
                profitLoss = profitLoss,
                successRate = successRate,
                entryCount = totalEntries,
                status = "COMPLETED"
            )
            sessionDao.update(updatedSession)
            Result.success(updatedSession)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
