package com.tradinganalytics.services.export

import java.util.Date

data class ReportData(
    val title: String = "Trading Report",
    val generatedAt: Date = Date(),
    val periodStart: Date? = null,
    val periodEnd: Date? = null,
    val totalTrades: Int = 0,
    val winningTrades: Int = 0,
    val losingTrades: Int = 0,
    val winRate: Double = 0.0,
    val totalProfitLoss: Double = 0.0,
    val averageProfit: Double = 0.0,
    val averageLoss: Double = 0.0,
    val largestWin: Double = 0.0,
    val largestLoss: Double = 0.0,
    val profitFactor: Double = 0.0,
    val sharpeRatio: Double = 0.0,
    val maxDrawdown: Double = 0.0,
    val averageHoldingPeriod: String = "N/A",
    val totalCommissions: Double = 0.0,
    val netProfitLoss: Double = 0.0,
    val startingBalance: Double = 0.0,
    val endingBalance: Double = 0.0,
    val returnPercentage: Double = 0.0,
    val bestDay: Date? = null,
    val bestDayPnL: Double = 0.0,
    val worstDay: Date? = null,
    val worstDayPnL: Double = 0.0,
    val entries: List<ReportEntry> = emptyList(),
    val patternStats: List<PatternStat> = emptyList(),
    val monthlyBreakdown: List<MonthlyBreakdown> = emptyList(),
    val notes: String = ""
)

data class ReportEntry(
    val date: Date,
    val type: String,
    val amount: Double,
    val result: String,
    val pattern: String? = null,
    val notes: String? = null
)

data class PatternStat(
    val name: String,
    val occurrences: Int,
    val wins: Int,
    val losses: Int,
    val winRate: Double,
    val totalPnL: Double
)

data class MonthlyBreakdown(
    val month: String,
    val year: Int,
    val trades: Int,
    val wins: Int,
    val losses: Int,
    val profitLoss: Double,
    val winRate: Double
)
