package com.tradinganalytics.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.tradinganalytics.data.database.entities.GoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(goal: GoalEntity): Long

    @Update
    suspend fun update(goal: GoalEntity)

    @Query("SELECT * FROM goals WHERE user_id = :userId AND is_completed = 0 ORDER BY date DESC LIMIT 1")
    suspend fun getActiveGoal(userId: Long): GoalEntity?

    @Query("SELECT * FROM goals WHERE user_id = :userId ORDER BY date DESC")
    fun getGoalHistory(userId: Long): Flow<List<GoalEntity>>
}
