package com.tradinganalytics.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tradinganalytics.data.database.entities.BackupEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BackupDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(backup: BackupEntity): Long

    @Query("SELECT * FROM backups ORDER BY created_at DESC")
    fun getAll(): Flow<List<BackupEntity>>

    @Delete
    suspend fun delete(backup: BackupEntity)

    @Query("SELECT * FROM backups WHERE file_name = :fileName LIMIT 1")
    suspend fun getByName(fileName: String): BackupEntity?
}
