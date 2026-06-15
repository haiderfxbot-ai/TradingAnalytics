package com.tradinganalytics.ui.history

import java.util.Date

data class HistoryUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val entries: List<HistoryEntryUi> = emptyList(),
    val filter: HistoryFilter = HistoryFilter.ALL,
    val searchQuery: String = "",
    val startDate: Long? = null,
    val endDate: Long? = null,
    val currentPage: Int = 0,
    val hasMore: Boolean = true,
    val error: String? = null
)

enum class HistoryFilter(val displayName: String) {
    ALL("All"),
    WINS("Wins"),
    LOSSES("Losses"),
    DATE_RANGE("Date Range")
}

data class HistoryEntryUi(
    val id: Long,
    val date: Date,
    val result: String,
    val amount: Double,
    val notes: String? = null,
    val patternName: String? = null,
    val sessionId: Long? = null
)
