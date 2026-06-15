package com.tradinganalytics.patterns.analyzer

import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt
import javax.inject.Inject

class AnalyticsEngine @Inject constructor() {

    fun analyzeTrends(entries: List<EntryEntity>): TrendAnalysis {
        if (entries.size < 2) {
            return TrendAnalysis(
                direction = "Neutral",
                strength = 0.0,
                slope = 0.0,
                volatility = 0.0,
                supportLevel = entries.firstOrNull()?.value ?: 0.0,
                resistanceLevel = entries.firstOrNull()?.value ?: 0.0,
                movingAverageShort = entries.firstOrNull()?.value ?: 0.0,
                movingAverageLong = entries.firstOrNull()?.value ?: 0.0,
                rsi = 50.0,
                macd = MacdInfo(0.0, 0.0, 0.0, false)
            )
        }

        val sorted = entries.sortedBy { it.timestamp }
        val values = sorted.map { it.value }
        val n = values.size

        val shortPeriod = minOf(10, n)
        val longPeriod = minOf(30, n)

        val maShort = values.takeLast(shortPeriod).average()
        val maLong = values.takeLast(longPeriod).average()

        val slope = calculateLinearRegressionSlope(values)

        val direction = when {
            slope > 0.5 -> "Bullish"
            slope < -0.5 -> "Bearish"
            else -> "Neutral"
        }

        val strength = abs(slope).coerceIn(0.0, 1.0)

        val returns = (1 until n).map { i ->
            if (values[i - 1] != 0.0) (values[i] - values[i - 1]) / values[i - 1] else 0.0
        }
        val meanReturn = if (returns.isNotEmpty()) returns.average() else 0.0
        val variance = if (returns.size > 1) {
            returns.map { (it - meanReturn).pow(2) }.sum() / (returns.size - 1)
        } else 0.0
        val volatility = sqrt(variance)

        val maxVal = values.max()
        val minVal = values.min()

        val rsi = calculateRSI(values)
        val macdInfo = calculateMACD(values)

        return TrendAnalysis(
            direction = direction,
            strength = strength,
            slope = slope,
            volatility = volatility,
            supportLevel = minVal,
            resistanceLevel = maxVal,
            movingAverageShort = maShort,
            movingAverageLong = maLong,
            rsi = rsi,
            macd = macdInfo
        )
    }

    fun calculateWinRate(entries: List<EntryEntity>): Double {
        if (entries.size < 2) return 0.0

        val sorted = entries.sortedBy { it.timestamp }
        var wins = 0
        var total = 0

        for (i in 1 until sorted.size) {
            val change = sorted[i].value - sorted[i - 1].value
            if (abs(change) > 0.0) {
                total++
                if (change > 0) wins++
            }
        }

        return if (total > 0) wins.toDouble() / total else 0.0
    }

    fun calculateLossRate(entries: List<EntryEntity>): Double {
        if (entries.size < 2) return 0.0

        val sorted = entries.sortedBy { it.timestamp }
        var losses = 0
        var total = 0

        for (i in 1 until sorted.size) {
            val change = sorted[i].value - sorted[i - 1].value
            if (abs(change) > 0.0) {
                total++
                if (change < 0) losses++
            }
        }

        return if (total > 0) losses.toDouble() / total else 0.0
    }

    fun detectStreaks(entries: List<EntryEntity>): StreakInfo {
        if (entries.size < 2) {
            return StreakInfo(0, 0, "None", 0, 0, 0, 0, 0.0)
        }

        val sorted = entries.sortedBy { it.timestamp }
        val changes = (1 until sorted.size).map { i ->
            sorted[i].value - sorted[i - 1].value
        }

        var currentStreak = 0
        var longestStreak = 0
        var streakType = "Neutral"
        var winStreak = 0
        var lossStreak = 0
        var consecutiveWins = 0
        var consecutiveLosses = 0
        var maxConsecutiveWins = 0
        var maxConsecutiveLosses = 0
        var winsDuringStreak = 0
        var totalDuringStreak = 0

        var i = changes.size - 1
        while (i >= 0) {
            if (changes[i] > 0) {
                if (streakType == "Neutral") {
                    streakType = "Winning"
                }
                if (streakType == "Winning") {
                    currentStreak++
                    winsDuringStreak++
                    totalDuringStreak++
                } else break
            } else if (changes[i] < 0) {
                if (streakType == "Neutral") {
                    streakType = "Losing"
                }
                if (streakType == "Losing") {
                    currentStreak++
                    totalDuringStreak++
                } else break
            } else break
            i--
        }

        for (change in changes) {
            if (change > 0) {
                consecutiveWins++
                consecutiveLosses = 0
                maxConsecutiveWins = maxOf(maxConsecutiveWins, consecutiveWins)
            } else if (change < 0) {
                consecutiveLosses++
                consecutiveWins = 0
                maxConsecutiveLosses = maxOf(maxConsecutiveLosses, consecutiveLosses)
            }
        }

        val streakWinRate = if (totalDuringStreak > 0) winsDuringStreak.toDouble() / totalDuringStreak else 0.0

        return StreakInfo(
            currentStreak = currentStreak,
            longestStreak = maxOf(maxConsecutiveWins, maxConsecutiveLosses),
            streakType = streakType,
            winStreak = consecutiveWins,
            lossStreak = consecutiveLosses,
            consecutiveWins = maxConsecutiveWins,
            consecutiveLosses = maxConsecutiveLosses,
            winRateDuringStreak = streakWinRate
        )
    }

    fun generateInsights(entries: List<EntryEntity>): List<Insight> {
        val insights = mutableListOf<Insight>()
        if (entries.size < 5) return insights

        val sorted = entries.sortedBy { it.timestamp }
        val values = sorted.map { it.value }
        val n = values.size
        val maShort = values.takeLast(10).average()
        val maLong = values.takeLast(30).average()
        val rsi = calculateRSI(values)
        val winRate = calculateWinRate(sorted)

        if (rsi > 70) {
            insights.add(
                Insight(
                    type = "Overbought",
                    severity = "Warning",
                    message = "RSI at $rsi indicates overbought conditions; potential reversal or pullback ahead",
                    confidence = minOf((rsi - 70) / 30.0, 1.0),
                    affectedEntries = sorted.takeLast(3).map { it.id },
                    suggestedAction = "Consider taking profits or tightening stop losses"
                )
            )
        }

        if (rsi < 30) {
            insights.add(
                Insight(
                    type = "Oversold",
                    severity = "Warning",
                    message = "RSI at $rsi indicates oversold conditions; potential bounce or reversal ahead",
                    confidence = minOf((30 - rsi) / 30.0, 1.0),
                    affectedEntries = sorted.takeLast(3).map { it.id },
                    suggestedAction = "Consider accumulation or preparing for long entry"
                )
            )
        }

        if (maShort > maLong && abs(maShort - maLong) / max(maLong, 0.01) > 0.02) {
            insights.add(
                Insight(
                    type = "GoldenCross",
                    severity = "Positive",
                    message = "Short-term MA ($maShort) above long-term MA ($maLong) indicating bullish trend",
                    confidence = 0.75,
                    affectedEntries = sorted.takeLast(5).map { it.id },
                    suggestedAction = "Maintain long positions or consider adding on pullbacks"
                )
            )
        }

        if (maShort < maLong && abs(maLong - maShort) / max(maLong, 0.01) > 0.02) {
            insights.add(
                Insight(
                    type = "DeathCross",
                    severity = "Negative",
                    message = "Short-term MA ($maShort) below long-term MA ($maLong) indicating bearish trend",
                    confidence = 0.75,
                    affectedEntries = sorted.takeLast(5).map { it.id },
                    suggestedAction = "Consider reducing long exposure or establishing hedges"
                )
            )
        }

        val recentVolatility = calculateVolatility(values.takeLast(10))
        if (recentVolatility > 0.5) {
            insights.add(
                Insight(
                    type = "HighVolatility",
                    severity = "Caution",
                    message = "Elevated volatility detected at $recentVolatility suggesting unstable market conditions",
                    confidence = 0.65,
                    affectedEntries = sorted.takeLast(5).map { it.id },
                    suggestedAction = "Reduce position size and widen stop losses"
                )
            )
        }

        val streakInfo = detectStreaks(sorted)
        if (streakInfo.currentStreak >= 5) {
            insights.add(
                Insight(
                    type = "StreakAlert",
                    severity = if (streakInfo.streakType == "Winning") "Positive" else "Negative",
                    message = "${streakInfo.currentStreak}-consecutive ${streakInfo.streakType.lowercase()} streak detected",
                    confidence = minOf(streakInfo.currentStreak / 10.0, 0.9),
                    affectedEntries = sorted.takeLast(streakInfo.currentStreak).map { it.id },
                    suggestedAction = if (streakInfo.streakType == "Winning") "Trend is strong; trail stops aggressively"
                    else "Avoid catching falling knife; wait for reversal confirmation"
                )
            )
        }

        if (winRate > 0.6) {
            insights.add(
                Insight(
                    type = "HighWinRate",
                    severity = "Positive",
                    message = "Win rate at ${"%.1f".format(winRate * 100)}% indicates strong directional performance",
                    confidence = 0.6,
                    affectedEntries = sorted.takeLast(10).map { it.id },
                    suggestedAction = "Continue current strategy with confidence"
                )
            )
        }

        if (winRate < 0.4) {
            insights.add(
                Insight(
                    type = "LowWinRate",
                    severity = "Caution",
                    message = "Win rate at ${"%.1f".format(winRate * 100)}% suggests unfavorable conditions",
                    confidence = 0.6,
                    affectedEntries = sorted.takeLast(10).map { it.id },
                    suggestedAction = "Reduce position size and reassess market conditions"
                )
            )
        }

        return insights
    }

    fun detectAnomalies(entries: List<EntryEntity>): List<Anomaly> {
        val anomalies = mutableListOf<Anomaly>()
        if (entries.size < 10) return anomalies

        val sorted = entries.sortedBy { it.timestamp }
        val values = sorted.map { it.value }
        val n = values.size

        val mean = values.average()
        val variance = values.map { (it - mean).pow(2) }.sum() / (n - 1).coerceAtLeast(1)
        val stdDev = sqrt(variance)

        if (stdDev == 0.0) return anomalies

        val sortedByValue = sorted.sortedBy { it.value }
        val q1 = sortedByValue[(n * 0.25).toInt().coerceIn(0, n - 1)].value
        val q3 = sortedByValue[(n * 0.75).toInt().coerceIn(0, n - 1)].value
        val iqr = q3 - q1
        val lowerFence = q1 - 1.5 * iqr
        val upperFence = q3 + 1.5 * iqr

        for (entry in sorted) {
            val zScore = abs(entry.value - mean) / stdDev

            if (zScore > 3.0 || entry.value < lowerFence || entry.value > upperFence) {
                val anomalyType = when {
                    entry.value > upperFence && entry.value > mean -> "SpikeHigh"
                    entry.value < lowerFence && entry.value < mean -> "SpikeLow"
                    entry.value > upperFence -> "OutlierHigh"
                    entry.value < lowerFence -> "OutlierLow"
                    else -> "StatisticalAnomaly"
                }

                val severity = when {
                    zScore > 4.0 || abs(entry.value - mean) > 3.0 * stdDev -> "Critical"
                    zScore > 3.0 || abs(entry.value - mean) > 2.5 * stdDev -> "High"
                    else -> "Medium"
                }

                anomalies.add(
                    Anomaly(
                        entryId = entry.id,
                        timestamp = entry.timestamp,
                        expectedValue = mean,
                        actualValue = entry.value,
                        deviation = entry.value - mean,
                        anomalyType = anomalyType,
                        severity = severity
                    )
                )
            }
        }

        val volumeMean = sorted.map { it.volume }.average()
        if (volumeMean > 0) {
            for (entry in sorted) {
                if (entry.volume > volumeMean * 3.0) {
                    val zVol = (entry.volume - volumeMean) / volumeMean
                    anomalies.add(
                        Anomaly(
                            entryId = entry.id,
                            timestamp = entry.timestamp,
                            expectedValue = volumeMean,
                            actualValue = entry.volume.toDouble(),
                            deviation = (entry.volume - volumeMean).toDouble(),
                            anomalyType = "VolumeSpike",
                            severity = if (zVol > 4.0) "Critical" else "High"
                        )
                    )
                }
            }
        }

        return anomalies
    }

    fun calculatePerformanceMetrics(entries: List<EntryEntity>): PerformanceMetrics {
        if (entries.size < 2) {
            return PerformanceMetrics(0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
        }

        val sorted = entries.sortedBy { it.timestamp }
        val values = sorted.map { it.value }
        val n = values.size

        val returns = (1 until n).map { i ->
            if (values[i - 1] != 0.0) (values[i] - values[i - 1]) / values[i - 1] else 0.0
        }

        val totalTrades = returns.size
        val wins = returns.filter { it > 0 }
        val losses = returns.filter { it < 0 }

        val winRate = if (totalTrades > 0) wins.size.toDouble() / totalTrades else 0.0
        val lossRate = if (totalTrades > 0) losses.size.toDouble() / totalTrades else 0.0

        val averageWin = if (wins.isNotEmpty()) wins.average() else 0.0
        val averageLoss = if (losses.isNotEmpty()) losses.average() else 0.0

        val grossProfit = wins.sum()
        val grossLoss = abs(losses.sum())
        val profitFactor = if (grossLoss > 0.0) grossProfit / grossLoss else if (grossProfit > 0) Double.MAX_VALUE else 0.0

        val meanReturn = if (returns.isNotEmpty()) returns.average() else 0.0
        val variance = if (returns.size > 1) {
            returns.map { (it - meanReturn).pow(2) }.sum() / (returns.size - 1)
        } else 0.0
        val volatility = if (variance > 0) sqrt(variance) else 0.0
        val sharpeRatio = if (volatility > 0) meanReturn / volatility * sqrt(252.0) else 0.0

        var peak = Double.MIN_VALUE
        var maxDrawdown = 0.0
        var cumulativeReturn = 1.0
        val cumulativeReturns = mutableListOf(1.0)

        for (ret in returns) {
            cumulativeReturn *= (1 + ret)
            cumulativeReturns.add(cumulativeReturn)
            if (cumulativeReturn > peak) {
                peak = cumulativeReturn
            }
            val drawdown = (peak - cumulativeReturn) / peak
            if (drawdown > maxDrawdown) {
                maxDrawdown = drawdown
            }
        }

        val totalReturn = if (values.first() != 0.0) {
            (values.last() - values.first()) / abs(values.first())
        } else 0.0

        return PerformanceMetrics(
            totalTrades = totalTrades,
            winRate = winRate,
            lossRate = lossRate,
            averageWin = averageWin,
            averageLoss = averageLoss,
            profitFactor = profitFactor,
            sharpeRatio = sharpeRatio,
            maxDrawdown = maxDrawdown,
            totalReturn = totalReturn,
            volatility = volatility
        )
    }

    private fun calculateLinearRegressionSlope(values: List<Double>): Double {
        val n = values.size
        if (n < 2) return 0.0

        val xMean = (0 until n).average()
        val yMean = values.average()

        var numerator = 0.0
        var denominator = 0.0

        for (i in 0 until n) {
            val xDiff = i - xMean
            val yDiff = values[i] - yMean
            numerator += xDiff * yDiff
            denominator += xDiff * xDiff
        }

        return if (denominator != 0.0) numerator / denominator else 0.0
    }

    private fun calculateRSI(values: List<Double>, period: Int = 14): Double {
        if (values.size < period + 1) return 50.0

        val recentValues = values.takeLast(period + 1)
        var gains = 0.0
        var losses = 0.0

        for (i in 1 until recentValues.size) {
            val change = recentValues[i] - recentValues[i - 1]
            if (change > 0) gains += change
            else losses += abs(change)
        }

        val avgGain = gains / period
        val avgLoss = losses / period

        if (avgLoss == 0.0) return 100.0
        if (avgGain == 0.0) return 0.0

        val rs = avgGain / avgLoss
        return 100.0 - (100.0 / (1.0 + rs))
    }

    private fun calculateMACD(values: List<Double>): MacdInfo {
        if (values.size < 26) return MacdInfo(0.0, 0.0, 0.0, false)

        val ema12 = calculateEMA(values, 12)
        val ema26 = calculateEMA(values, 26)
        val macdLine = ema12 - ema26

        val macdValues = mutableListOf(macdLine)
        val signalLine = calculateEMA(macdValues, 9)
        val histogram = macdLine - signalLine

        return MacdInfo(
            macdLine = macdLine,
            signalLine = signalLine,
            histogram = histogram,
            isBullish = macdLine > signalLine
        )
    }

    private fun calculateEMA(values: List<Double>, period: Int): Double {
        if (values.size < period) return values.average()

        val multiplier = 2.0 / (period + 1)
        var ema = values.take(period).average()

        for (i in period until values.size) {
            ema = (values[i] - ema) * multiplier + ema
        }

        return ema
    }

    private fun calculateVolatility(values: List<Double>): Double {
        if (values.size < 2) return 0.0

        val returns = (1 until values.size).map { i ->
            if (values[i - 1] != 0.0) (values[i] - values[i - 1]) / values[i - 1] else 0.0
        }

        val mean = returns.average()
        val variance = returns.map { (it - mean).pow(2) }.sum() / (returns.size - 1)
        return sqrt(variance)
    }
}
