package com.tradinganalytics.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "patterns")
data class PatternEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "pattern_id")
    val patternId: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "category")
    val category: String,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "detection_rules")
    val detectionRules: String,
    @ColumnInfo(name = "confidence_formula")
    val confidenceFormula: String,
    @ColumnInfo(name = "risk_rating")
    val riskRating: String,
    @ColumnInfo(name = "times_detected")
    val timesDetected: Int = 0,
    @ColumnInfo(name = "times_successful")
    val timesSuccessful: Int = 0,
    @ColumnInfo(name = "historical_accuracy")
    val historicalAccuracy: Double = 0.0,
    @ColumnInfo(name = "average_confidence")
    val averageConfidence: Double = 0.0
)
