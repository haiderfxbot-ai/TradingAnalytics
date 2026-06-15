package com.tradinganalytics.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "pattern_matches",
    indices = [Index("pattern_id"), Index("entry_id")]
)
data class PatternMatchEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "pattern_id")
    val patternId: String,
    @ColumnInfo(name = "similarity_score")
    val similarityScore: Double,
    @ColumnInfo(name = "confidence_level")
    val confidenceLevel: String,
    @ColumnInfo(name = "matched_date")
    val matchedDate: Date,
    @ColumnInfo(name = "entry_id")
    val entryId: Long? = null
)
