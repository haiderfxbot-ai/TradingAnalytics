package com.tradinganalytics.ui.dashboard

import com.tradinganalytics.domain.model.GoalProgress

data class DashboardUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val username: String = "",
    val greeting: String = "",
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
    val lastActivity: Long? = null,
    val winStreak: Int = 0,
    val lossStreak: Int = 0,
    val error: String? = null
)

sealed class DashboardEvent {
    data object Refresh : DashboardEvent()
    data class Error(val message: String) : DashboardEvent()
}
