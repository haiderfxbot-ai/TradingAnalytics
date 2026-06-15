package com.tradinganalytics.ui.patternlib

import com.tradinganalytics.data.database.entities.PatternMatchEntity
import com.tradinganalytics.patterns.library.PatternDefinition

enum class PatternFilter(val displayName: String) {
    ALL("All"),
    TREND("Trend"),
    REVERSAL("Reversal"),
    REPEATING("Repeating"),
    FREQUENCY("Frequency"),
    TIMING("Timing"),
    PROBABILITY("Probability"),
    MOMENTUM("Momentum"),
    STATISTICAL("Statistical")
}

enum class PatternViewMode {
    LIST, GRID
}

data class PatternLibUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val patterns: List<PatternDefinition> = emptyList(),
    val filteredPatterns: List<PatternDefinition> = emptyList(),
    val searchQuery: String = "",
    val selectedFilter: PatternFilter = PatternFilter.ALL,
    val selectedRiskLevel: String? = null,
    val viewMode: PatternViewMode = PatternViewMode.GRID,
    val favoriteIds: Set<String> = emptySet(),
    val recentlyMatched: List<PatternMatchEntity> = emptyList(),
    val error: String? = null
)

data class PatternDetailUiState(
    val isLoading: Boolean = true,
    val pattern: PatternDefinition? = null,
    val isFavorite: Boolean = false,
    val matchHistory: List<PatternMatchEntity> = emptyList(),
    val notes: List<String> = emptyList(),
    val noteInput: String = "",
    val isAnalyzing: Boolean = false,
    val analysisResult: String? = null,
    val error: String? = null
)

sealed class PatternLibEvent {
    data class OnSearchQueryChange(val query: String) : PatternLibEvent()
    data class OnFilterSelect(val filter: PatternFilter) : PatternLibEvent()
    data class OnRiskLevelSelect(val riskLevel: String?) : PatternLibEvent()
    data object ToggleViewMode : PatternLibEvent()
    data class ToggleFavorite(val patternId: String) : PatternLibEvent()
    data class OnPatternClick(val patternId: String) : PatternLibEvent()
    data object Refresh : PatternLibEvent()
    data object DismissError : PatternLibEvent()
}
