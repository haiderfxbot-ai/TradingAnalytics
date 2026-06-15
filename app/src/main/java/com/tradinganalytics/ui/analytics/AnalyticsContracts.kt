package com.tradinganalytics.ui.analytics

import com.tradinganalytics.domain.model.PatternPerformance
import com.tradinganalytics.domain.model.PerformanceRecord

data class AnalyticsUiState(
    val isLoading: Boolean = true,
    val selectedTab: AnalyticsTab = AnalyticsTab.OVERVIEW,
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
    val activePatterns: List<PatternPerformance> = emptyList(),
    val recentMatches: Int = 0,
    val trendInfo: TrendInfo = TrendInfo(),
    val performanceInfo: PerformanceInfo = PerformanceInfo(),
    val error: String? = null
)

enum class AnalyticsTab(val displayName: String) {
    OVERVIEW("Overview"),
    PATTERNS("Patterns"),
    TRENDS("Trends"),
    PERFORMANCE("Performance")
}

data class TrendInfo(
    val direction: String = "Neutral",
    val strength: Double = 0.0,
    val slope: Double = 0.0,
    val volatility: Double = 0.0,
    val isImproving: Boolean = false,
    val isDeclining: Boolean = false,
    val rsi: Double = 50.0,
    val macdBullish: Boolean = false
)

data class PerformanceInfo(
    val bestDay: PerformanceRecord? = null,
    val worstDay: PerformanceRecord? = null,
    val bestStreak: Int = 0,
    val worstStreak: Int = 0,
    val currentStreak: Int = 0,
    val streakType: String = "None",
    val bestStreakType: String = "",
    val worstStreakType: String = ""
)
