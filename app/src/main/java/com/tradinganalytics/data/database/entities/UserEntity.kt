package com.tradinganalytics.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "username")
    val username: String,
    @ColumnInfo(name = "password_hash")
    val passwordHash: String,
    @ColumnInfo(name = "password_salt")
    val passwordSalt: String,
    @ColumnInfo(name = "display_name")
    val displayName: String,
    @ColumnInfo(name = "role")
    val role: String,
    @ColumnInfo(name = "status")
    val status: String,
    @ColumnInfo(name = "profile_image_path")
    val profileImagePath: String? = null,
    @ColumnInfo(name = "created_at")
    val createdAt: Date,
    @ColumnInfo(name = "last_login")
    val lastLogin: Date? = null,
    @ColumnInfo(name = "is_logged_in")
    val isLoggedIn: Boolean = false
)
