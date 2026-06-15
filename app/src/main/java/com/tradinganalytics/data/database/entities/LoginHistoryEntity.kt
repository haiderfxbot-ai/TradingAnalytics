package com.tradinganalytics.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "login_history")
data class LoginHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "username")
    val username: String,
    @ColumnInfo(name = "login_time")
    val loginTime: Date,
    @ColumnInfo(name = "logout_time")
    val logoutTime: Date? = null,
    @ColumnInfo(name = "session_id")
    val sessionId: String,
    @ColumnInfo(name = "device_info")
    val deviceInfo: String
)
