package com.tradinganalytics.ui.analytics

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tradinganalytics.core.utils.SessionManager
import com.tradinganalytics.data.database.entities.EntryEntity
import com.tradinganalytics.data.repository.EntryRepository
import com.tradinganalytics.data.repository.PatternRepository
import com.tradinganalytics.domain.model.PatternPerformance
import com.tradinganalytics.domain.model.PerformanceRecord
import com.tradinganalytics.patterns.analyzer.AnalyticsEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val entryRepository: EntryRepository,
    private val patternRepository: PatternRepository,
    private val analyticsEngine: AnalyticsEngine,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val sessionManager = SessionManager(context)
    private val userId: Long get() = sessionManager.getUserId()

    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState.asStateFlow()

    init {
        loadAnalytics()
    }

    fun selectTab(tab: AnalyticsTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun refresh() {
        loadAnalytics()
    }

    private fun loadAnalytics() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val entriesDeferred = async { entryRepository.getHistory(userId).first() }
                val patternsDeferred = async { patternRepository.getAllPatterns().first() }

                val entries = entriesDeferred.await()
                val patterns = patternsDeferred.await()

                val wins = entries.count { it.result == "WIN" }
                val losses = entries.count { it.result == "LOSS" }
                val total = entries.size
                val successRate = if (total > 0) (wins.toDouble() / total) * 100 else 0.0

                val totalProfit = entries.filter { it.result == "WIN" }.sumOf { it.amount }
                val totalLoss = entries.filter { it.result == "LOSS" }.sumOf { it.amount }
                val profitLossRatio = if (totalLoss > 0) totalProfit / totalLoss else if (totalProfit > 0) Double.MAX_VALUE else 0.0

                val averageResult = if (total > 0) {
                    entries.sumOf { if (it.result == "WIN") it.amount else -it.amount } / total
                } else 0.0

                val dailyRate = calculateRateForPeriod(entries, Calendar.DAY_OF_YEAR, 1)
                val weeklyRate = calculateRateForPeriod(entries, Calendar.WEEK_OF_YEAR, 1)
                val monthlyRate = calculateRateForPeriod(entries, Calendar.MONTH, 1)
                val lifetimeRate = successRate

                val analysisEntries = entries.toAnalysisEntries()
                val trends = analyticsEngine.analyzeTrends(analysisEntries)
                val metrics = analyticsEngine.calculatePerformanceMetrics(analysisEntries)
                val streakInfo = analyticsEngine.detectStreaks(analysisEntries)

                val activePatterns = patterns.map { pattern ->
                    PatternPerformance(
                        patternName = pattern.name,
                        category = pattern.category,
                        confidence = pattern.averageConfidence,
                        similarity = 0.0,
                        timesDetected = pattern.timesDetected,
                        timesSuccessful = pattern.timesSuccessful,
                        historicalAccuracy = pattern.historicalAccuracy
                    )
                }.sortedByDescending { it.confidence }

                val activePats = patterns.filter { it.timesDetected > 0 }.map { it.name }
                val recentMatches = patterns.sumOf { it.timesDetected }

                val trendInfo = TrendInfo(
                    direction = trends.direction,
                    strength = trends.strength,
                    slope = trends.slope,
                    volatility = trends.volatility,
                    isImproving = trends.slope > 0.5,
                    isDeclining = trends.slope < -0.5,
                    rsi = trends.rsi,
                    macdBullish = trends.macd.isBullish
                )

                val bestDay = entries
                    .groupBy { calendarDate(it.date) }
                    .mapValues { (_, dayEntries) ->
                        dayEntries.sumOf { e -> if (e.result == "WIN") e.amount else -e.amount }
                    }
                    .maxByOrNull { it.value }
                    ?.let { PerformanceRecord(
                        label = it.key,
                        value = it.value,
                        date = Date(),
                        type = "BEST"
                    ) }

                val worstDay = entries
                    .groupBy { calendarDate(it.date) }
                    .mapValues { (_, dayEntries) ->
                        dayEntries.sumOf { e -> if (e.result == "WIN") e.amount else -e.amount }
                    }
                    .minByOrNull { it.value }
                    ?.let { PerformanceRecord(
                        label = it.key,
                        value = it.value,
                        date = Date(),
                        type = "WORST"
                    ) }

                val performanceInfo = PerformanceInfo(
                    bestDay = bestDay,
                    worstDay = worstDay,
                    bestStreak = streakInfo.consecutiveWins,
                    worstStreak = streakInfo.consecutiveLosses,
                    currentStreak = streakInfo.currentStreak,
                    streakType = streakInfo.streakType,
                    bestStreakType = "Winning",
                    worstStreakType = "Losing"
                )

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        totalWins = wins,
                        totalLosses = losses,
                        totalEntries = total,
                        successRate = successRate,
                        profitLossRatio = profitLossRatio,
                        goalCompletionRate = if (total > 0) wins.toDouble() / total else 0.0,
                        averageResult = averageResult,
                        dailyRate = dailyRate,
                        weeklyRate = weeklyRate,
                        monthlyRate = monthlyRate,
                        lifetimeRate = lifetimeRate,
                        activePatterns = activePatterns,
                        recentMatches = recentMatches,
                        trendInfo = trendInfo,
                        performanceInfo = performanceInfo,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = e.message ?: "Unknown error")
                }
            }
        }
    }

    private fun calculateRateForPeriod(entries: List<EntryEntity>, field: Int, count: Int): Double {
        val cal = Calendar.getInstance()
        cal.add(field, -count)
        val cutoff = cal.time
        val periodEntries = entries.filter { !it.date.before(cutoff) }
        val total = periodEntries.size
        if (total == 0) return 0.0
        val wins = periodEntries.count { it.result == "WIN" }
        return (wins.toDouble() / total) * 100
    }

    private fun calendarDate(date: Date): String {
        val cal = Calendar.getInstance()
        cal.time = date
        return "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.MONTH) + 1}-${cal.get(Calendar.DAY_OF_MONTH)}"
    }
}

private fun List<EntryEntity>.toAnalysisEntries(): List<com.tradinganalytics.patterns.analyzer.EntryEntity> {
    val sorted = this.sortedBy { it.date }
    return sorted.map { entry ->
        com.tradinganalytics.patterns.analyzer.EntryEntity(
            id = entry.id.toString(),
            timestamp = entry.date.time,
            value = if (entry.result == "WIN") entry.amount else -entry.amount,
            volume = 0,
            high = entry.amount,
            low = 0.0,
            open = 0.0,
            close = entry.amount,
            label = entry.result
        )
    }
}
