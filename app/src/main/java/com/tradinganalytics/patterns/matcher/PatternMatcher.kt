package com.tradinganalytics.patterns.matcher

import com.tradinganalytics.patterns.analyzer.AnalyticsEngine
import com.tradinganalytics.patterns.analyzer.EntryEntity
import com.tradinganalytics.patterns.analyzer.PatternMatch
import com.tradinganalytics.patterns.library.PatternDefinition
import com.tradinganalytics.patterns.library.PatternLibrary
import kotlin.math.abs
import kotlin.math.ln
import kotlin.math.min
import kotlin.math.max
import kotlin.math.sqrt

class PatternMatcher {

    private val analyticsEngine = AnalyticsEngine()

    fun analyzeEntries(entries: List<EntryEntity>): List<PatternMatch> {
        if (entries.size < 3) return emptyList()

        val matches = mutableListOf<PatternMatch>()
        val patterns = PatternLibrary.allPatterns

        val sortedEntries = entries.sortedBy { it.timestamp }

        for (pattern in patterns) {
            val similarity = calculateSimilarityMultiEntry(sortedEntries, pattern)
            if (similarity > 0.15) {
                val confidence = calculateConfidence(similarity, pattern, sortedEntries)
                val match = PatternMatch(
                    patternId = pattern.patternId,
                    patternName = pattern.name,
                    category = pattern.category,
                    similarity = similarity,
                    confidence = confidence,
                    riskLevel = getRiskLevel(confidence),
                    matchedEntries = sortedEntries.takeLast(pattern.detectionRules.minSequenceLength),
                    timestamp = sortedEntries.last().timestamp
                )
                matches.add(match)
            }
        }

        return rankPatterns(matches)
    }

    fun calculateSimilarity(entry: EntryEntity, pattern: PatternDefinition): Double {
        val valueScore = evaluateValuePattern(entry.value, pattern)
        val volumeScore = evaluateVolumePattern(entry.volume, pattern)
        val rangeScore = evaluateRangePattern(entry.high - entry.low, pattern)
        val directionalScore = evaluateDirectionalPattern(entry.close - entry.open, pattern)

        val weights = pattern.detectionRules.weightage
        val baseWeight = 1.0 / max(weights.size, 1)

        val weightedScore = (valueScore * 0.35 + volumeScore * 0.25 + rangeScore * 0.20 + directionalScore * 0.20)

        return weightedScore.coerceIn(0.0, 1.0)
    }

    private fun calculateSimilarityMultiEntry(entries: List<EntryEntity>, pattern: PatternDefinition): Double {
        val window = entries.takeLast(pattern.detectionRules.minSequenceLength)
        if (window.size < pattern.detectionRules.minSequenceLength) return 0.0

        val individualScores = window.map { calculateSimilarity(it, pattern) }
        val avgSimilarity = individualScores.average()

        val sequenceScore = evaluateSequence(window, pattern)
        val trendAlignment = evaluateTrendAlignment(window, pattern)

        return (avgSimilarity * 0.4 + sequenceScore * 0.35 + trendAlignment * 0.25).coerceIn(0.0, 1.0)
    }

    fun rankPatterns(matches: List<PatternMatch>): List<PatternMatch> {
        return matches.sortedByDescending { it.confidence }
    }

    fun getConfidenceLevel(score: Double): String {
        return when {
            score >= 0.85 -> "Very High"
            score >= 0.70 -> "High"
            score >= 0.50 -> "Moderate"
            score >= 0.30 -> "Low"
            else -> "Very Low"
        }
    }

    fun getRiskLevel(score: Double): String {
        return when {
            score >= 0.85 -> "Very Low"
            score >= 0.70 -> "Low"
            score >= 0.50 -> "Moderate"
            score >= 0.30 -> "High"
            else -> "Very High"
        }
    }

    private fun calculateConfidence(similarity: Double, pattern: PatternDefinition, entries: List<EntryEntity>): Double {
        val trendAnalysis = analyticsEngine.analyzeTrends(entries)
        val trendConfidence = when {
            trendAnalysis.strength > 0.7 -> 0.9
            trendAnalysis.strength > 0.5 -> 0.7
            trendAnalysis.strength > 0.3 -> 0.5
            else -> 0.3
        }

        val volumeConfidence = entries.takeLast(5).map { it.volume }.average().let { avgVol ->
            if (avgVol > 1000) 0.85 else if (avgVol > 500) 0.65 else 0.40
        }

        val volatilityAdjustment = if (trendAnalysis.volatility < 0.3) 0.1 else if (trendAnalysis.volatility < 0.6) 0.0 else -0.1

        val rawConfidence = similarity * 0.5 + trendConfidence * 0.25 + volumeConfidence * 0.25 + volatilityAdjustment
        return rawConfidence.coerceIn(0.0, 1.0)
    }

    private fun evaluateValuePattern(value: Double, pattern: PatternDefinition): Double {
        val normalizedValue = abs(value) / (abs(value) + 100.0)
        val patternHash = pattern.patternId.hashCode()
        val bias = (patternHash % 100) / 100.0
        val score = 1.0 - abs(normalizedValue - bias)
        return score.coerceIn(0.0, 1.0)
    }

    private fun evaluateVolumePattern(volume: Long, pattern: PatternDefinition): Double {
        val normalizedVolume = ln(max(volume.toDouble(), 1.0) + 1.0) / ln(1000000.0)
        val expectedVolume = when {
            pattern.category.contains("Frequency", ignoreCase = true) -> 0.7
            pattern.category.contains("Momentum", ignoreCase = true) -> 0.65
            pattern.category.contains("Reversal", ignoreCase = true) -> 0.6
            else -> 0.5
        }
        return (1.0 - abs(normalizedVolume - expectedVolume)).coerceIn(0.0, 1.0)
    }

    private fun evaluateRangePattern(range: Double, pattern: PatternDefinition): Double {
        val normalizedRange = min(range / 100.0, 1.0)
        val expectedRange = when {
            pattern.category.contains("Statistical", ignoreCase = true) -> 0.3
            pattern.category.contains("Timing", ignoreCase = true) -> 0.4
            pattern.category.contains("Momentum", ignoreCase = true) -> 0.6
            else -> 0.5
        }
        return (1.0 - abs(normalizedRange - expectedRange)).coerceIn(0.0, 1.0)
    }

    private fun evaluateDirectionalPattern(direction: Double, pattern: PatternDefinition): Double {
        val directionStrength = direction.coerceIn(-10.0, 10.0) / 10.0
        val expectedDirection = when {
            pattern.category.contains("Trend", ignoreCase = true) -> 1.0
            pattern.name.startsWith("Bullish", ignoreCase = true) ||
            pattern.name.startsWith("Golden", ignoreCase = true) ||
            pattern.name.startsWith("Upside", ignoreCase = true) -> 1.0
            pattern.name.startsWith("Bearish", ignoreCase = true) ||
            pattern.name.startsWith("Death", ignoreCase = true) ||
            pattern.name.startsWith("Downside", ignoreCase = true) -> -1.0
            else -> 0.0
        }
        return (1.0 - abs(directionStrength - expectedDirection) / 2.0).coerceIn(0.0, 1.0)
    }

    private fun evaluateSequence(entries: List<EntryEntity>, pattern: PatternDefinition): Double {
        if (entries.size < 2) return 0.0

        var sequenceScore = 0.0
        val recentEntries = entries.takeLast(5)

        for (i in 1 until recentEntries.size) {
            val prev = recentEntries[i - 1]
            val curr = recentEntries[i]

            when {
                pattern.category.contains("Trend", ignoreCase = true) -> {
                    if (curr.value > prev.value) sequenceScore += 0.25
                }
                pattern.category.contains("Reversal", ignoreCase = true) -> {
                    val recentAvg = recentEntries.take(3).map { it.value }.average()
                    if (abs(curr.value - recentAvg) / max(recentAvg, 0.01) > 0.02) sequenceScore += 0.2
                }
                pattern.category.contains("Repeating", ignoreCase = true) -> {
                    val values = recentEntries.map { it.value }
                    val alternating = (0 until values.size - 2).any { j ->
                        (values[j] > values[j + 1] && values[j + 1] < values[j + 2]) ||
                        (values[j] < values[j + 1] && values[j + 1] > values[j + 2])
                    }
                    if (alternating) sequenceScore += 0.3
                }
                pattern.category.contains("Frequency", ignoreCase = true) -> {
                    val volRatio = if (prev.volume > 0) curr.volume.toDouble() / prev.volume else 1.0
                    if (volRatio > 1.5) sequenceScore += 0.25
                }
                pattern.category.contains("Timing", ignoreCase = true) -> {
                    val timeGap = curr.timestamp - prev.timestamp
                    if (timeGap in 300_000..3_600_000) sequenceScore += 0.2
                }
                pattern.category.contains("Probability", ignoreCase = true) -> {
                    val change = abs(curr.value - prev.value) / max(prev.value, 0.01)
                    if (change > 0.01 && change < 0.05) sequenceScore += 0.2
                }
                pattern.category.contains("Momentum", ignoreCase = true) -> {
                    val change = curr.value - prev.value
                    val prevChange = if (i >= 2) recentEntries[i - 1].value - recentEntries[i - 2].value else 0.0
                    if (abs(change) > abs(prevChange)) sequenceScore += 0.25
                }
                pattern.category.contains("Statistical", ignoreCase = true) -> {
                    val values = recentEntries.map { it.value }
                    val mean = values.average()
                    val variance = values.map { (it - mean) * (it - mean) }.average()
                    val stdDev = sqrt(variance)
                    if (stdDev > 0.0 && abs(curr.value - mean) / stdDev > 1.0) sequenceScore += 0.2
                }
            }
        }

        return (sequenceScore / recentEntries.size.coerceAtLeast(1)).coerceIn(0.0, 1.0)
    }

    private fun evaluateTrendAlignment(entries: List<EntryEntity>, pattern: PatternDefinition): Double {
        if (entries.size < 3) return 0.5

        val recent = entries.takeLast(5)
        val values = recent.map { it.value }
        val n = values.size

        val xMean = (0 until n).average()
        val yMean = values.average()
        var num = 0.0
        var den = 0.0
        for (i in 0 until n) {
            num += (i - xMean) * (values[i] - yMean)
            den += (i - xMean) * (i - xMean)
        }
        val slope = if (den != 0.0) num / den else 0.0

        val expectedDirection = when {
            pattern.category.contains("Trend", ignoreCase = true) -> 1.0
            pattern.name.startsWith("Bullish") || pattern.name.startsWith("Golden") -> 1.0
            pattern.name.startsWith("Bearish") || pattern.name.startsWith("Death") -> -1.0
            else -> 0.0
        }

        val alignment = if (expectedDirection != 0.0) {
            slope.sign == expectedDirection.sign
        } else {
            abs(slope) < 0.1
        }

        return if (alignment) 0.8 else 0.3
    }
}
