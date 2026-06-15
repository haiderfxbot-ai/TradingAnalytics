package com.tradinganalytics.ui.patternlib

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tradinganalytics.data.database.dao.PatternMatchDao
import com.tradinganalytics.data.database.entities.PatternMatchEntity
import com.tradinganalytics.data.preferences.AppPreferences
import com.tradinganalytics.data.repository.PatternRepository
import com.tradinganalytics.patterns.analyzer.AnalyticsEngine
import com.tradinganalytics.patterns.analyzer.EntryEntity
import com.tradinganalytics.patterns.analyzer.PatternMatch
import com.tradinganalytics.patterns.analyzer.TrendAnalysis
import com.tradinganalytics.patterns.library.PatternDefinition
import com.tradinganalytics.patterns.library.PatternLibrary
import com.tradinganalytics.patterns.matcher.PatternMatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PatternLibViewModel @Inject constructor(
    private val patternRepository: PatternRepository,
    private val patternMatcher: PatternMatcher,
    private val analyticsEngine: AnalyticsEngine,
    private val patternMatchDao: PatternMatchDao,
    private val appPreferences: AppPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(PatternLibUiState())
    val uiState: StateFlow<PatternLibUiState> = _uiState.asStateFlow()

    private val _detailState = MutableStateFlow(PatternDetailUiState())
    val detailState: StateFlow<PatternDetailUiState> = _detailState.asStateFlow()

    private val allPatterns: List<PatternDefinition> = PatternLibrary.allPatterns

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val favoriteIds = appPreferences.getFavoritePatternIds().first().toSet()
                val recentMatches = patternMatchDao.getRecentMatches(20).first()
                val current = _uiState.value
                val filtered = filterPatterns(
                    allPatterns, current.searchQuery, current.selectedFilter, current.selectedRiskLevel
                )
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        patterns = allPatterns,
                        filteredPatterns = filtered,
                        favoriteIds = favoriteIds,
                        recentlyMatched = recentMatches
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = e.message ?: "Failed to load patterns")
                }
            }
        }
    }

    fun onEvent(event: PatternLibEvent) {
        when (event) {
            is PatternLibEvent.OnSearchQueryChange -> onSearchQueryChange(event.query)
            is PatternLibEvent.OnFilterSelect -> onFilterSelect(event.filter)
            is PatternLibEvent.OnRiskLevelSelect -> onRiskLevelSelect(event.riskLevel)
            is PatternLibEvent.ToggleViewMode -> toggleViewMode()
            is PatternLibEvent.ToggleFavorite -> toggleFavorite(event.patternId)
            is PatternLibEvent.OnPatternClick -> loadPatternDetail(event.patternId)
            is PatternLibEvent.Refresh -> refresh()
            is PatternLibEvent.DismissError -> _uiState.update { it.copy(error = null) }
            is PatternLibEvent.NoteChange -> _detailState.update { it.copy(noteInput = event.query) }
            is PatternLibEvent.AddNote -> addNote()
            is PatternLibEvent.AnalyzePattern -> analyzePattern(event.patternId)
        }
    }

    private fun onSearchQueryChange(query: String) {
        _uiState.update { state ->
            state.copy(
                searchQuery = query,
                filteredPatterns = filterPatterns(state.patterns, query, state.selectedFilter, state.selectedRiskLevel)
            )
        }
    }

    private fun onFilterSelect(filter: PatternFilter) {
        _uiState.update { state ->
            state.copy(
                selectedFilter = filter,
                filteredPatterns = filterPatterns(state.patterns, state.searchQuery, filter, state.selectedRiskLevel)
            )
        }
    }

    private fun onRiskLevelSelect(riskLevel: String?) {
        _uiState.update { state ->
            state.copy(
                selectedRiskLevel = riskLevel,
                filteredPatterns = filterPatterns(state.patterns, state.searchQuery, state.selectedFilter, riskLevel)
            )
        }
    }

    private fun toggleViewMode() {
        _uiState.update { state ->
            state.copy(
                viewMode = if (state.viewMode == PatternViewMode.GRID) PatternViewMode.LIST else PatternViewMode.GRID
            )
        }
    }

    fun getPatternById(patternId: String): PatternDefinition? =
        allPatterns.find { it.patternId == patternId }

    fun hasPatternId(patternId: String): Boolean =
        allPatterns.any { it.patternId == patternId }

    fun runAnalysis(
        patternId: String,
        entries: List<EntryEntity>
    ): Pair<List<PatternMatch>, TrendAnalysis> {
        val matches = patternMatcher.analyzeEntries(entries)
        val trend = analyticsEngine.analyzeTrends(entries)
        return matches to trend
    }

    private fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            try {
                val favoriteIds = appPreferences.getFavoritePatternIds().first().toSet()
                val recentMatches = patternMatchDao.getRecentMatches(20).first()
                _uiState.update {
                    it.copy(isRefreshing = false, favoriteIds = favoriteIds, recentlyMatched = recentMatches)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isRefreshing = false, error = e.message ?: "Refresh failed")
                }
            }
        }
    }

    private fun toggleFavorite(patternId: String) {
        viewModelScope.launch {
            val current = _uiState.value.favoriteIds.toMutableSet()
            if (current.contains(patternId)) current.remove(patternId) else current.add(patternId)
            appPreferences.setFavoritePatternIds(current.toList())
            _uiState.update { it.copy(favoriteIds = current) }
        }
    }

    private fun loadPatternDetail(patternId: String) {
        viewModelScope.launch {
            _detailState.update { it.copy(isLoading = true, error = null) }
            try {
                val pattern = allPatterns.find { it.patternId == patternId }
                val matches = patternMatchDao.getByPatternId(patternId).first()
                val favoriteIds = appPreferences.getFavoritePatternIds().first().toSet()
                _detailState.update {
                    it.copy(
                        isLoading = false,
                        pattern = pattern,
                        isFavorite = favoriteIds.contains(patternId),
                        matchHistory = matches,
                        notes = emptyList()
                    )
                }
            } catch (e: Exception) {
                _detailState.update {
                    it.copy(isLoading = false, error = e.message ?: "Failed to load pattern")
                }
            }
        }
    }

    private fun addNote() {
        viewModelScope.launch {
            val note = _detailState.value.noteInput
            if (note.isBlank()) return@launch
            val currentNotes = _detailState.value.notes.toMutableList()
            currentNotes.add(note)
            _detailState.update { it.copy(notes = currentNotes, noteInput = "") }
        }
    }

    private fun analyzePattern(patternId: String) {
        viewModelScope.launch {
            _detailState.update { it.copy(isAnalyzing = true) }
            try {
                val pattern = allPatterns.find { it.patternId == patternId }
                val result = pattern?.let { "Analysis complete for: ${it.name}" } ?: "Pattern not found"
                _detailState.update { it.copy(isAnalyzing = false, analysisResult = result) }
            } catch (e: Exception) {
                _detailState.update {
                    it.copy(isAnalyzing = false, analysisResult = "Analysis failed: ${e.message}")
                }
            }
        }
    }

    private fun filterPatterns(
        patterns: List<PatternDefinition>,
        query: String,
        filter: PatternFilter,
        riskLevel: String?
    ): List<PatternDefinition> {
        return patterns.filter { p ->
            val matchesQuery = query.isBlank() ||
                p.name.contains(query, ignoreCase = true) ||
                p.description.contains(query, ignoreCase = true) ||
                p.category.contains(query, ignoreCase = true)
            val matchesCategory = filter == PatternFilter.ALL ||
                p.category.startsWith(filter.displayName, ignoreCase = true)
            val matchesRisk = riskLevel == null || p.riskRating.equals(riskLevel, ignoreCase = true)
            matchesQuery && matchesCategory && matchesRisk
        }
    }
}
