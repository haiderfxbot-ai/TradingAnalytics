package com.tradinganalytics.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tradinganalytics.data.database.entities.LoginHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LoginHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(loginHistory: LoginHistoryEntity): Long

    @Query("SELECT * FROM login_history WHERE username = :username ORDER BY login_time DESC")
    fun getAllByUser(username: String): Flow<List<LoginHistoryEntity>>

    @Query("SELECT * FROM login_history WHERE username = :username ORDER BY login_time DESC LIMIT 1")
    suspend fun getLatest(username: String): LoginHistoryEntity?

    @Delete
    suspend fun delete(loginHistory: LoginHistoryEntity)
}
