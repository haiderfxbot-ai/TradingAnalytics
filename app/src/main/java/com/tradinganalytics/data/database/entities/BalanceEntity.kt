package com.tradinganalytics.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "balances",
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
data class BalanceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "user_id")
    val userId: Long,
    @ColumnInfo(name = "balance")
    val balance: Double,
    @ColumnInfo(name = "previous_balance")
    val previousBalance: Double,
    @ColumnInfo(name = "change_amount")
    val changeAmount: Double,
    @ColumnInfo(name = "change_type")
    val changeType: String,
    @ColumnInfo(name = "reason")
    val reason: String,
    @ColumnInfo(name = "date")
    val date: Date
)
