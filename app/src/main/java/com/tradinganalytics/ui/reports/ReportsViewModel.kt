package com.tradinganalytics.ui.reports

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tradinganalytics.core.utils.SessionManager
import com.tradinganalytics.data.database.entities.EntryEntity
import com.tradinganalytics.data.database.entities.SessionEntity
import com.tradinganalytics.data.repository.EntryRepository
import com.tradinganalytics.data.repository.SessionRepository
import com.tradinganalytics.storage.StorageManager
import com.google.gson.GsonBuilder
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val entryRepository: EntryRepository,
    private val sessionRepository: SessionRepository,
    private val sessionManager: SessionManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val storageManager = StorageManager(context)
    private val gson = GsonBuilder().setPrettyPrinting().create()

    private val _uiState = MutableStateFlow(ReportsUiState())
    val uiState: StateFlow<ReportsUiState> = _uiState.asStateFlow()

    private val _navigation = MutableSharedFlow<ReportsNavigation>()
    val navigation: SharedFlow<ReportsNavigation> = _navigation.asSharedFlow()

    init {
        generateReport(DateRange.TODAY)
    }

    fun onEvent(event: ReportsEvent) {
        when (event) {
            is ReportsEvent.SelectDateRange -> generateReport(event.dateRange)
            is ReportsEvent.SelectReportType -> _uiState.update {
                it.copy(selectedReportType = event.reportType)
            }
            is ReportsEvent.ExportJson -> exportJson()
            is ReportsEvent.ExportCsv -> exportCsv()
            is ReportsEvent.SetCustomDateRange -> setCustomDateRange(event.startDate, event.endDate)
            is ReportsEvent.DismissError -> _uiState.update { it.copy(error = null) }
            is ReportsEvent.ClearSnackbar -> _uiState.update { it.copy(snackbarMessage = null) }
        }
    }

    private fun generateReport(dateRange: DateRange) {
        val userId = sessionManager.getUserId()
        if (userId <= 0) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, selectedDateRange = dateRange, error = null) }

            try {
                val (startDate, endDate) = calculateDateRange(dateRange)

                _uiState.update {
                    it.copy(
                        customStartDate = startDate,
                        customEndDate = endDate
                    )
                }

                var entries: List<EntryEntity> = emptyList()
                var sessions: List<SessionEntity> = emptyList()

                entryRepository.getByDateRange(userId, startDate, endDate).collect { entryList ->
                    entries = entryList
                }

                sessionRepository.getSessionHistory(userId).collect { sessionList ->
                    sessions = sessionList.filter { session ->
                        session.startTime.after(startDate) || session.startTime == startDate
                    }.filter { session ->
                        (session.endTime ?: session.startTime).before(endDate) ||
                            (session.endTime ?: session.startTime) == endDate
                    }
                }

                val totalEntries = entries.size
                val wins = entries.count { it.result == "WIN" }
                val losses = entries.count { it.result == "LOSS" }
                val winRate = if (totalEntries > 0) (wins.toDouble() / totalEntries) * 100 else 0.0
                val totalProfit = entries.filter { it.result == "WIN" }.sumOf { it.amount }
                val totalLoss = entries.filter { it.result == "LOSS" }.sumOf { it.amount }
                val netPnL = totalProfit - totalLoss
                val totalSessions = sessions.size
                val activeSessions = sessions.count { it.status == "ACTIVE" }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        summary = ReportSummary(
                            totalEntries = totalEntries,
                            wins = wins,
                            losses = losses,
                            winRate = winRate,
                            totalProfit = totalProfit,
                            totalLoss = totalLoss,
                            netPnL = netPnL,
                            totalSessions = totalSessions,
                            activeSessions = activeSessions
                        ),
                        entries = entries,
                        lastUpdated = System.currentTimeMillis()
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = e.message ?: "Failed to generate report")
                }
            }
        }
    }

    private fun setCustomDateRange(startDate: Date, endDate: Date) {
        _uiState.update {
            it.copy(customStartDate = startDate, customEndDate = endDate)
        }
        val dateRange = _uiState.value.selectedDateRange
        if (dateRange == DateRange.CUSTOM) {
            generateReport(DateRange.CUSTOM)
        }
    }

    private fun calculateDateRange(dateRange: DateRange): Pair<Date, Date> {
        val today = LocalDate.now()
        val startDate: LocalDate
        val endDate: LocalDate = today.plusDays(1)

        startDate = when (dateRange) {
            DateRange.TODAY -> today
            DateRange.WEEK -> today.minus(7, ChronoUnit.DAYS)
            DateRange.MONTH -> today.minus(1, ChronoUnit.MONTHS)
            DateRange.CUSTOM -> {
                val state = _uiState.value
                return Pair(state.customStartDate, state.customEndDate)
            }
        }

        return Pair(
            Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant()),
            Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant())
        )
    }

    private fun exportJson() {
        viewModelScope.launch {
            try {
                val state = _uiState.value
                val exportData = mapOf(
                    "report_type" to state.selectedReportType.name,
                    "date_range" to state.selectedDateRange.name,
                    "generated_at" to System.currentTimeMillis(),
                    "summary" to mapOf(
                        "total_entries" to state.summary.totalEntries,
                        "wins" to state.summary.wins,
                        "losses" to state.summary.losses,
                        "win_rate" to state.summary.winRate,
                        "total_profit" to state.summary.totalProfit,
                        "total_loss" to state.summary.totalLoss,
                        "net_pnl" to state.summary.netPnL,
                        "total_sessions" to state.summary.totalSessions
                    ),
                    "entries" to state.entries.map { entry ->
                        mapOf(
                            "id" to entry.id,
                            "amount" to entry.amount,
                            "result" to entry.result,
                            "notes" to entry.notes,
                            "timestamp" to entry.timestamp.time
                        )
                    }
                )

                val json = gson.toJson(exportData)
                val exportsDir = storageManager.getDirectory(StorageManager.StorageDirectory.Exports)
                val fileName = "report_${state.selectedReportType.name.lowercase()}_${System.currentTimeMillis()}.json"
                val file = File(exportsDir, fileName)
                file.writeText(json)

                _uiState.update {
                    it.copy(snackbarMessage = "Exported to: $fileName")
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(snackbarMessage = e.message ?: "Failed to export JSON")
                }
            }
        }
    }

    private fun exportCsv() {
        viewModelScope.launch {
            try {
                val state = _uiState.value
                val header = "ID,Amount,Result,Notes,Timestamp"
                val rows = state.entries.joinToString("\n") { entry ->
                    "${entry.id},${entry.amount},${entry.result},\"${entry.notes}\",${entry.timestamp.time}"
                }
                val csv = "$header\n$rows"

                val exportsDir = storageManager.getDirectory(StorageManager.StorageDirectory.Exports)
                val fileName = "report_${state.selectedReportType.name.lowercase()}_${System.currentTimeMillis()}.csv"
                val file = File(exportsDir, fileName)
                file.writeText(csv)

                _uiState.update {
                    it.copy(snackbarMessage = "Exported to: $fileName")
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(snackbarMessage = e.message ?: "Failed to export CSV")
                }
            }
        }
    }
}

data class ReportSummary(
    val totalEntries: Int = 0,
    val wins: Int = 0,
    val losses: Int = 0,
    val winRate: Double = 0.0,
    val totalProfit: Double = 0.0,
    val totalLoss: Double = 0.0,
    val netPnL: Double = 0.0,
    val totalSessions: Int = 0,
    val activeSessions: Int = 0
)

data class ReportsUiState(
    val isLoading: Boolean = false,
    val selectedDateRange: DateRange = DateRange.TODAY,
    val selectedReportType: ReportType = ReportType.SUMMARY,
    val summary: ReportSummary = ReportSummary(),
    val entries: List<EntryEntity> = emptyList(),
    val customStartDate: Date = Date(),
    val customEndDate: Date = Date(),
    val lastUpdated: Long = 0L,
    val error: String? = null,
    val snackbarMessage: String? = null
)

enum class DateRange {
    TODAY, WEEK, MONTH, CUSTOM
}

enum class ReportType {
    SUMMARY, DETAILED, SESSIONS
}

sealed class ReportsEvent {
    data class SelectDateRange(val dateRange: DateRange) : ReportsEvent()
    data class SelectReportType(val reportType: ReportType) : ReportsEvent()
    data object ExportJson : ReportsEvent()
    data object ExportCsv : ReportsEvent()
    data class SetCustomDateRange(val startDate: Date, val endDate: Date) : ReportsEvent()
    data object DismissError : ReportsEvent()
    data object ClearSnackbar : ReportsEvent()
}

sealed class ReportsNavigation
