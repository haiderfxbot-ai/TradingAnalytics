package com.tradinganalytics.patterns.analyzer

data class EntryEntity(
    val id: String,
    val timestamp: Long,
    val value: Double,
    val volume: Long = 0,
    val high: Double = value,
    val low: Double = value,
    val open: Double = value,
    val close: Double = value,
    val label: String = ""
)

data class TrendAnalysis(
    val direction: String,
    val strength: Double,
    val slope: Double,
    val volatility: Double,
    val supportLevel: Double,
    val resistanceLevel: Double,
    val movingAverageShort: Double,
    val movingAverageLong: Double,
    val rsi: Double,
    val macd: MacdInfo
)

data class MacdInfo(
    val macdLine: Double,
    val signalLine: Double,
    val histogram: Double,
    val isBullish: Boolean
)

data class StreakInfo(
    val currentStreak: Int,
    val longestStreak: Int,
    val streakType: String,
    val winStreak: Int,
    val lossStreak: Int,
    val consecutiveWins: Int,
    val consecutiveLosses: Int,
    val winRateDuringStreak: Double
)

data class Insight(
    val type: String,
    val severity: String,
    val message: String,
    val confidence: Double,
    val affectedEntries: List<String>,
    val suggestedAction: String
)

data class Anomaly(
    val entryId: String,
    val timestamp: Long,
    val expectedValue: Double,
    val actualValue: Double,
    val deviation: Double,
    val anomalyType: String,
    val severity: String
)

data class PerformanceMetrics(
    val totalTrades: Int,
    val winRate: Double,
    val lossRate: Double,
    val averageWin: Double,
    val averageLoss: Double,
    val profitFactor: Double,
    val sharpeRatio: Double,
    val maxDrawdown: Double,
    val totalReturn: Double,
    val volatility: Double
)

data class PatternMatch(
    val patternId: String,
    val patternName: String,
    val category: String,
    val similarity: Double,
    val confidence: Double,
    val riskLevel: String,
    val matchedEntries: List<EntryEntity>,
    val timestamp: Long
)
