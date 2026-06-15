package com.tradinganalytics.ui.history

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tradinganalytics.core.utils.SessionManager
import com.tradinganalytics.data.repository.EntryRepository
import com.tradinganalytics.data.repository.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val entryRepository: EntryRepository,
    private val sessionRepository: SessionRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val sessionManager = SessionManager(context)
    private val userId: Long get() = sessionManager.getUserId()

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    companion object {
        private const val PAGE_SIZE = 20
    }

    init {
        loadEntries()
    }

    fun setFilter(filter: HistoryFilter) {
        _uiState.update { it.copy(filter = filter, currentPage = 0, entries = emptyList()) }
        loadEntries()
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query, currentPage = 0, entries = emptyList()) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            loadEntries()
        }
    }

    fun setDateRange(startDate: Long?, endDate: Long?) {
        _uiState.update { it.copy(startDate = startDate, endDate = endDate, currentPage = 0, entries = emptyList()) }
        loadEntries()
    }

    fun loadNextPage() {
        if (_uiState.value.hasMore && !_uiState.value.isLoading) {
            _uiState.update { it.copy(currentPage = it.currentPage + 1) }
            loadEntries()
        }
    }

    fun refresh() {
        _uiState.update { it.copy(isRefreshing = true, currentPage = 0, entries = emptyList()) }
        loadEntries()
    }

    private fun loadEntries() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val state = _uiState.value
                val allEntries = when (state.filter) {
                    HistoryFilter.ALL -> entryRepository.getHistory(userId).first()
                    HistoryFilter.WINS -> entryRepository.getHistory(userId).first()
                        .filter { it.result == "WIN" }
                    HistoryFilter.LOSSES -> entryRepository.getHistory(userId).first()
                        .filter { it.result == "LOSS" }
                    HistoryFilter.DATE_RANGE -> {
                        val start = state.startDate?.let { java.util.Date(it) }
                            ?: java.util.Date(0)
                        val end = state.endDate?.let { java.util.Date(it) }
                            ?: java.util.Date()
                        entryRepository.getByDateRange(userId, start, end).first()
                    }
                }

                val filtered = if (state.searchQuery.isNotBlank()) {
                    allEntries.filter { entry ->
                        entry.notes?.contains(state.searchQuery, ignoreCase = true) == true ||
                            entry.result.contains(state.searchQuery, ignoreCase = true) ||
                            entry.amount.toString().contains(state.searchQuery)
                    }
                } else {
                    allEntries
                }

                val sorted = filtered.sortedByDescending { it.date }
                val pageSize = PAGE_SIZE
                val startIndex = 0
                val endIndex = minOf(startIndex + pageSize * (state.currentPage + 1), sorted.size)
                val pagedEntries = sorted.subList(startIndex, endIndex)

                val uiEntries = pagedEntries.map { entry ->
                    HistoryEntryUi(
                        id = entry.id,
                        date = entry.date,
                        result = entry.result,
                        amount = entry.amount,
                        notes = entry.notes,
                        patternName = entry.patternId,
                        sessionId = entry.sessionId
                    )
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        entries = uiEntries,
                        hasMore = endIndex < sorted.size,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        error = e.message ?: "Unknown error"
                    )
                }
            }
        }
    }
}
