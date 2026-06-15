package com.tradinganalytics.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.tradinganalytics.data.database.entities.SessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: SessionEntity): Long

    @Update
    suspend fun update(session: SessionEntity)

    @Query("SELECT * FROM sessions WHERE user_id = :userId ORDER BY start_time DESC")
    fun getAllByUser(userId: Long): Flow<List<SessionEntity>>

    @Query("SELECT * FROM sessions WHERE user_id = :userId AND status = 'ACTIVE' ORDER BY start_time DESC LIMIT 1")
    suspend fun getActiveSession(userId: Long): SessionEntity?
}
