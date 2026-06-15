package com.tradinganalytics.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tradinganalytics.data.database.entities.BalanceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BalanceDao {
    @Query("SELECT * FROM balances")
    fun getAll(): Flow<List<BalanceEntity>>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(balance: BalanceEntity): Long

    @Query("SELECT * FROM balances WHERE user_id = :userId ORDER BY date DESC")
    fun getAllByUser(userId: Long): Flow<List<BalanceEntity>>

    @Query("SELECT balance FROM balances WHERE user_id = :userId ORDER BY date DESC LIMIT 1")
    suspend fun getCurrentBalance(userId: Long): Double?

    @Query("SELECT * FROM balances WHERE user_id = :userId ORDER BY date ASC")
    fun getBalanceHistory(userId: Long): Flow<List<BalanceEntity>>
}
