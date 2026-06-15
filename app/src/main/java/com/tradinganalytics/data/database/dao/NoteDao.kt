package com.tradinganalytics.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.tradinganalytics.data.database.entities.NoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: NoteEntity): Long

    @Update
    suspend fun update(note: NoteEntity)

    @Delete
    suspend fun delete(note: NoteEntity)

    @Query("SELECT * FROM notes WHERE user_id = :userId ORDER BY date DESC")
    fun getAllByUser(userId: Long): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE user_id = :userId AND content LIKE '%' || :query || '%' ORDER BY date DESC")
    fun search(userId: Long, query: String): Flow<List<NoteEntity>>
}
