package com.tradinganalytics.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "entries",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("user_id"), Index("session_id"), Index("result")]
)
data class EntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "user_id")
    val userId: Long,
    @ColumnInfo(name = "session_id")
    val sessionId: Long? = null,
    @ColumnInfo(name = "date")
    val date: Date,
    @ColumnInfo(name = "result")
    val result: String,
    @ColumnInfo(name = "amount")
    val amount: Double,
    @ColumnInfo(name = "notes")
    val notes: String? = null,
    @ColumnInfo(name = "pattern_id")
    val patternId: String? = null
)
