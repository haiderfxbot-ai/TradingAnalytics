package com.tradinganalytics.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tradinganalytics.data.database.entities.EntryEntity
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface EntryDao {
    @Query("SELECT * FROM entries")
    fun getAll(): Flow<List<EntryEntity>>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: EntryEntity): Long

    @Query("SELECT * FROM entries WHERE user_id = :userId ORDER BY date DESC")
    fun getAllByUser(userId: Long): Flow<List<EntryEntity>>

    @Query("SELECT * FROM entries WHERE user_id = :userId AND date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getByDateRange(userId: Long, startDate: Date, endDate: Date): Flow<List<EntryEntity>>

    @Query("SELECT * FROM entries WHERE user_id = :userId AND result = :result ORDER BY date DESC")
    fun getByResult(userId: Long, result: String): Flow<List<EntryEntity>>

    @Query("SELECT * FROM entries WHERE user_id = :userId AND notes LIKE '%' || :query || '%' ORDER BY date DESC")
    fun searchByNotes(userId: Long, query: String): Flow<List<EntryEntity>>
}
