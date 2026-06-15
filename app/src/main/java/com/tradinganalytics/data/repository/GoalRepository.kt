package com.tradinganalytics.data.repository

import com.tradinganalytics.data.database.dao.GoalDao
import com.tradinganalytics.data.database.entities.GoalEntity
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoalRepository @Inject constructor(
    private val goalDao: GoalDao
) {

    suspend fun getActiveGoal(userId: Long): Result<GoalEntity?> {
        return try {
            Result.success(goalDao.getActiveGoal(userId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getGoalHistory(userId: Long): Flow<List<GoalEntity>> = goalDao.getGoalHistory(userId)

    suspend fun setGoal(
        userId: Long,
        targetAmount: Double,
        goalType: String,
        currentAmount: Double = 0.0
    ): Result<GoalEntity> {
        return try {
            val activeGoal = goalDao.getActiveGoal(userId)
            if (activeGoal != null) {
                return Result.failure(IllegalStateException("An active goal already exists"))
            }

            val goal = GoalEntity(
                userId = userId,
                targetAmount = targetAmount,
                currentAmount = currentAmount,
                goalType = goalType,
                date = Date()
            )
            val id = goalDao.insert(goal)
            Result.success(goal.copy(id = id))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProgress(userId: Long, amount: Double): Result<GoalEntity> {
        return try {
            val goal = goalDao.getActiveGoal(userId)
                ?: return Result.failure(IllegalStateException("No active goal found"))

            val updatedGoal = goal.copy(
                currentAmount = goal.currentAmount + amount
            )
            goalDao.update(updatedGoal)
            Result.success(updatedGoal)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun checkCompletion(userId: Long): Result<GoalEntity?> {
        return try {
            val goal = goalDao.getActiveGoal(userId) ?: return Result.success(null)
            if (goal.currentAmount >= goal.targetAmount) {
                val completed = goal.copy(
                    isCompleted = true,
                    completedAt = Date()
                )
                goalDao.update(completed)
                Result.success(completed)
            } else {
                Result.success(goal)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
