package com.tradinganalytics.ui.dashboard

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tradinganalytics.core.utils.SessionManager
import com.tradinganalytics.data.database.entities.EntryEntity
import com.tradinganalytics.data.repository.BalanceRepository
import com.tradinganalytics.data.repository.EntryRepository
import com.tradinganalytics.data.repository.GoalRepository
import com.tradinganalytics.data.repository.PatternRepository
import com.tradinganalytics.data.repository.SessionRepository
import com.tradinganalytics.domain.model.GoalProgress
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
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val balanceRepository: BalanceRepository,
    private val goalRepository: GoalRepository,
    private val sessionRepository: SessionRepository,
    private val entryRepository: EntryRepository,
    private val patternRepository: PatternRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val sessionManager = SessionManager(context)
    private val userId: Long get() = sessionManager.getUserId()

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboard()
    }

    fun refreshDashboard() {
        _uiState.update { it.copy(isRefreshing = true) }
        loadDashboard()
    }

    private fun loadDashboard() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val balanceDeferred = async { balanceRepository.getBalance(userId) }
                val entriesDeferred = async { entryRepository.getHistory(userId).first() }
                val goalDeferred = async { goalRepository.getActiveGoal(userId) }
                val patternsDeferred = async { patternRepository.getAllPatterns().first() }
                val sessionsDeferred = async { sessionRepository.getSessionHistory(userId).first() }

                val balanceResult = balanceDeferred.await()
                val entries = entriesDeferred.await()
                val goalResult = goalDeferred.await()
                val patterns = patternsDeferred.await()

                val currentBalance = balanceResult.getOrDefault(0.0)
                val username = sessionManager.getUsername() ?: "Trader"

                val wins = entries.count { it.result == "WIN" }
                val losses = entries.count { it.result == "LOSS" }
                val total = entries.size
                val successRate = if (total > 0) (wins.toDouble() / total) * 100 else 0.0

                val todayPnL = calculateDailyPnL(entries)
                val totalPnL = entries.sumOf { if (it.result == "WIN") it.amount else -it.amount }

                val goal = goalResult.getOrNull()
                val goalProgress = if (goal != null) {
                    GoalProgress(
                        currentAmount = goal.currentAmount,
                        targetAmount = goal.targetAmount,
                        percentage = if (goal.targetAmount > 0) (goal.currentAmount / goal.targetAmount) * 100 else 0.0,
                        isCompleted = goal.isCompleted
                    )
                } else GoalProgress()

                val lossAmount = entries.filter { it.result == "LOSS" }.sumOf { it.amount }
                val totalAmount = entries.sumOf { it.amount }
                val riskScore = if (totalAmount > 0) (lossAmount / totalAmount) * 100 else 0.0
                val riskLevel = when {
                    riskScore > 50.0 -> "HIGH"
                    riskScore > 25.0 -> "MEDIUM"
                    else -> "LOW"
                }

                val activePattern = patterns.maxByOrNull { it.averageConfidence }?.name

                val lastActivity = entries.maxOfOrNull { it.date }?.time

                val streakInfo = calculateStreaks(entries)

                val greeting = getGreeting()

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                        username = username,
                        greeting = greeting,
                        currentBalance = currentBalance,
                        dailyPnL = todayPnL,
                        totalPnL = totalPnL,
                        goalProgress = goalProgress,
                        riskLevel = riskLevel,
                        riskScore = riskScore,
                        winCount = wins,
                        lossCount = losses,
                        successRate = successRate,
                        activePattern = activePattern,
                        lastActivity = lastActivity,
                        winStreak = streakInfo.first,
                        lossStreak = streakInfo.second,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, isRefreshing = false, error = e.message ?: "Unknown error")
                }
            }
        }
    }

    private fun calculateDailyPnL(entries: List<EntryEntity>): Double {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val todayStart = cal.time
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        val todayEnd = cal.time
        return entries
            .filter { !it.date.before(todayStart) && !it.date.after(todayEnd) }
            .sumOf { if (it.result == "WIN") it.amount else -it.amount }
    }

    private fun calculateStreaks(entries: List<EntryEntity>): Pair<Int, Int> {
        val sorted = entries.sortedByDescending { it.date }
        var winStreak = 0
        var lossStreak = 0
        for (entry in sorted) {
            when (entry.result) {
                "WIN" -> {
                    winStreak++
                    lossStreak = 0
                }
                "LOSS" -> {
                    lossStreak++
                    winStreak = 0
                }
            }
        }
        return Pair(winStreak, lossStreak)
    }

    private fun getGreeting(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 0..11 -> "Good Morning"
            in 12..16 -> "Good Afternoon"
            else -> "Good Evening"
        }
    }
}
