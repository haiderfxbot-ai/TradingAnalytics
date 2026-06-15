package com.tradinganalytics.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tradinganalytics.data.database.entities.PatternEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PatternDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pattern: PatternEntity): Long

    @Query("SELECT * FROM patterns ORDER BY name ASC")
    fun getAll(): Flow<List<PatternEntity>>

    @Query("SELECT * FROM patterns WHERE category = :category ORDER BY name ASC")
    fun getByCategory(category: String): Flow<List<PatternEntity>>

    @Query("SELECT * FROM patterns WHERE risk_rating = :riskRating ORDER BY name ASC")
    fun getByRiskLevel(riskRating: String): Flow<List<PatternEntity>>

    @Query("SELECT * FROM patterns WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchByName(query: String): Flow<List<PatternEntity>>
}
