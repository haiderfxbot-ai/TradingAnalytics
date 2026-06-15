package com.tradinganalytics.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "sessions",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("user_id")]
)
data class SessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "user_id")
    val userId: Long,
    @ColumnInfo(name = "start_time")
    val startTime: Date,
    @ColumnInfo(name = "end_time")
    val endTime: Date? = null,
    @ColumnInfo(name = "profit_loss")
    val profitLoss: Double = 0.0,
    @ColumnInfo(name = "success_rate")
    val successRate: Double = 0.0,
    @ColumnInfo(name = "entry_count")
    val entryCount: Int = 0,
    @ColumnInfo(name = "status")
    val status: String
)
