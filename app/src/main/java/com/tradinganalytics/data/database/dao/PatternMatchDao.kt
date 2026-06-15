package com.tradinganalytics.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tradinganalytics.data.database.entities.PatternMatchEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PatternMatchDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(match: PatternMatchEntity): Long

    @Query("SELECT * FROM pattern_matches WHERE pattern_id = :patternId ORDER BY matched_date DESC")
    fun getByPatternId(patternId: String): Flow<List<PatternMatchEntity>>

    @Query("SELECT * FROM pattern_matches ORDER BY matched_date DESC LIMIT :limit")
    fun getRecentMatches(limit: Int): Flow<List<PatternMatchEntity>>
}
