package com.tradinganalytics.data.repository

import com.tradinganalytics.data.database.dao.PatternDao
import com.tradinganalytics.data.database.entities.PatternEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PatternRepository @Inject constructor(
    private val patternDao: PatternDao
) {

    fun getAllPatterns(): Flow<List<PatternEntity>> = patternDao.getAll()

    fun getByCategory(category: String): Flow<List<PatternEntity>> =
        patternDao.getByCategory(category)

    fun searchPatterns(query: String): Flow<List<PatternEntity>> =
        patternDao.searchByName(query)

    suspend fun updatePatternStats(
        patternId: String,
        wasSuccessful: Boolean,
        confidence: Double
    ): Result<PatternEntity> {
        return try {
            val patterns = patternDao.getAll().first().ifEmpty {
                return Result.failure(IllegalStateException("Patterns not loaded"))
            }
            val pattern = patterns.find { it.patternId == patternId }
                ?: return Result.failure(IllegalArgumentException("Pattern not found"))

            val newTimesDetected = pattern.timesDetected + 1
            val newTimesSuccessful = pattern.timesSuccessful + (if (wasSuccessful) 1 else 0)
            val newAccuracy = if (newTimesDetected > 0) {
                (newTimesSuccessful.toDouble() / newTimesDetected) * 100
            } else 0.0
            val newAvgConfidence = ((pattern.averageConfidence * pattern.timesDetected) + confidence) / newTimesDetected

            val updated = pattern.copy(
                timesDetected = newTimesDetected,
                timesSuccessful = newTimesSuccessful,
                historicalAccuracy = newAccuracy,
                averageConfidence = newAvgConfidence
            )
            patternDao.insert(updated)
            Result.success(updated)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
