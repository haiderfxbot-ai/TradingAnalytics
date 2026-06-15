package com.tradinganalytics.domain.model

import java.util.Date

data class DashboardSummary(
    val currentBalance: Double = 0.0,
    val dailyPnL: Double = 0.0,
    val totalPnL: Double = 0.0,
    val goalProgress: GoalProgress = GoalProgress(),
    val riskLevel: String = "LOW",
    val riskScore: Double = 0.0,
    val winCount: Int = 0,
    val lossCount: Int = 0,
    val successRate: Double = 0.0,
    val activePattern: String? = null,
    val lastActivity: Date? = null,
    val winStreak: Int = 0,
    val lossStreak: Int = 0
)

data class AnalyticsSummary(
    val totalWins: Int = 0,
    val totalLosses: Int = 0,
    val totalEntries: Int = 0,
    val successRate: Double = 0.0,
    val profitLossRatio: Double = 0.0,
    val goalCompletionRate: Double = 0.0,
    val averageResult: Double = 0.0,
    val dailyRate: Double = 0.0,
    val weeklyRate: Double = 0.0,
    val monthlyRate: Double = 0.0,
    val lifetimeRate: Double = 0.0,
    val activePatterns: List<String> = emptyList(),
    val recentMatches: Int = 0,
    val trendDirection: String = "Neutral",
    val trendStrength: Double = 0.0,
    val topPatterns: List<PatternPerformance> = emptyList()
)

data class GoalProgress(
    val currentAmount: Double = 0.0,
    val targetAmount: Double = 0.0,
    val percentage: Double = 0.0,
    val isCompleted: Boolean = false
)

data class RiskInfo(
    val level: String = "LOW",
    val score: Double = 0.0,
    val description: String = "No risk data available"
)

data class StreakInfo(
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val streakType: String = "None",
    val winRateDuringStreak: Double = 0.0
)

data class PerformanceRecord(
    val label: String = "",
    val value: Double = 0.0,
    val date: Date = Date(),
    val type: String = ""
)

data class PatternPerformance(
    val patternName: String = "",
    val category: String = "",
    val confidence: Double = 0.0,
    val similarity: Double = 0.0,
    var timesDetected: Int = 0,
    var timesSuccessful: Int = 0,
    val historicalAccuracy: Double = 0.0
)
