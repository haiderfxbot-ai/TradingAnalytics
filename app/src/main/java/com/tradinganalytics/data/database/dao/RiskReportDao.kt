package com.tradinganalytics.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tradinganalytics.data.database.entities.RiskReportEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RiskReportDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(report: RiskReportEntity): Long

    @Query("SELECT * FROM risk_reports WHERE user_id = :userId ORDER BY date DESC")
    fun getByUser(userId: Long): Flow<List<RiskReportEntity>>

    @Query("SELECT * FROM risk_reports WHERE user_id = :userId ORDER BY date DESC LIMIT 1")
    suspend fun getLatest(userId: Long): RiskReportEntity?
}
