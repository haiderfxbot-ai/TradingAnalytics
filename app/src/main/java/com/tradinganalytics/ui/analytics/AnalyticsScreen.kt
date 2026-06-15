package com.tradinganalytics.ui.analytics

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tradinganalytics.core.theme.DarkThemeColors
import com.tradinganalytics.domain.model.PatternPerformance
import com.tradinganalytics.domain.model.PerformanceRecord

@Composable
fun AnalyticsScreen(
    uiState: AnalyticsUiState,
    onTabSelected: (AnalyticsTab) -> Unit,
    onRefresh: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TabRow(
            selectedTabIndex = uiState.selectedTab.ordinal,
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary,
            indicator = { tabPositions ->
                Box(
                    modifier = Modifier
                        .tabIndicatorOffset(tabPositions[uiState.selectedTab.ordinal])
                        .height(2.dp)
                        .fillMaxWidth()
                        .background(DarkThemeColors.Primary)
                )
            },
            divider = {}
        ) {
            AnalyticsTab.entries.forEach { tab ->
                Tab(
                    selected = uiState.selectedTab == tab,
                    onClick = { onTabSelected(tab) },
                    text = {
                        Text(
                            text = tab.displayName,
                            style = MaterialTheme.typography.labelLarge,
                            color = if (uiState.selectedTab == tab)
                                DarkThemeColors.Primary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
            }
        }

        if (uiState.isLoading) {
            AnalyticsSkeleton()
        } else {
            AnimatedContent(
                targetState = uiState.selectedTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "tab_content"
            ) { tab ->
                when (tab) {
                    AnalyticsTab.OVERVIEW -> OverviewTab(uiState)
                    AnalyticsTab.PATTERNS -> PatternsTab(uiState)
                    AnalyticsTab.TRENDS -> TrendsTab(uiState)
                    AnalyticsTab.PERFORMANCE -> PerformanceTab(uiState)
                }
            }
        }
    }
}

@Composable
private fun OverviewTab(uiState: AnalyticsUiState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SummaryCard(
                icon = Icons.Default.CheckCircle,
                label = "Wins",
                value = "${uiState.totalWins}",
                valueColor = DarkThemeColors.ChartGreen,
                modifier = Modifier.weight(1f)
            )
            SummaryCard(
                icon = Icons.Default.Close,
                label = "Losses",
                value = "${uiState.totalLosses}",
                valueColor = DarkThemeColors.ChartRed,
                modifier = Modifier.weight(1f)
            )
            SummaryCard(
                icon = Icons.Default.RemoveRedEye,
                label = "Entries",
                value = "${uiState.totalEntries}",
                valueColor = DarkThemeColors.Info,
                modifier = Modifier.weight(1f)
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                DarkThemeColors.GlassBackground,
                                DarkThemeColors.GlassHighlight
                            )
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(20.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Success Rate",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier.size(140.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            progress = (uiState.successRate / 100f).toFloat().coerceIn(0f, 1f),
                            color = DarkThemeColors.Primary,
                            trackColor = DarkThemeColors.GlassBackground,
                            strokeWidth = 10.dp
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "%.1f%%".format(uiState.successRate),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${uiState.totalWins}/${uiState.totalEntries}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DetailCard(
                label = "P/L Ratio",
                value = if (uiState.profitLossRatio == Double.MAX_VALUE) "∞"
                else "%.2f".format(uiState.profitLossRatio),
                modifier = Modifier.weight(1f)
            )
            DetailCard(
                label = "Avg Result",
                value = "%.2f".format(uiState.averageResult),
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DetailCard(
                label = "Goal Completion",
                value = "%.1f%%".format(uiState.goalCompletionRate * 100),
                modifier = Modifier.weight(1f)
            )
            DetailCard(
                label = "Recent Matches",
                value = "${uiState.recentMatches}",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun PatternsTab(uiState: AnalyticsUiState) {
    if (uiState.activePatterns.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No patterns detected yet",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(uiState.activePatterns) { pattern ->
                PatternCard(pattern = pattern)
            }
        }
    }
}

@Composable
private fun TrendsTab(uiState: AnalyticsUiState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TrendDirectionCard(uiState.trendInfo)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DetailCard(
                label = "RSI",
                value = "%.1f".format(uiState.trendInfo.rsi),
                modifier = Modifier.weight(1f)
            )
            DetailCard(
                label = "Volatility",
                value = "%.4f".format(uiState.trendInfo.volatility),
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DetailCard(
                label = "Daily Rate",
                value = "%.1f%%".format(uiState.dailyRate),
                modifier = Modifier.weight(1f)
            )
            DetailCard(
                label = "Weekly Rate",
                value = "%.1f%%".format(uiState.weeklyRate),
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DetailCard(
                label = "Monthly Rate",
                value = "%.1f%%".format(uiState.monthlyRate),
                modifier = Modifier.weight(1f)
            )
            DetailCard(
                label = "Lifetime Rate",
                value = "%.1f%%".format(uiState.lifetimeRate),
                modifier = Modifier.weight(1f)
            )
        }

        if (uiState.trendInfo.macdBullish) {
            TrendSignalBadge(text = "MACD Bullish Crossover", positive = true)
        } else {
            TrendSignalBadge(text = "MACD Bearish Crossover", positive = false)
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun PerformanceTab(uiState: AnalyticsUiState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Best & Worst Records",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        uiState.performanceInfo.bestDay?.let {
            PerformanceRecordCard(
                title = "Best Day",
                record = it,
                isPositive = true
            )
        }

        uiState.performanceInfo.worstDay?.let {
            PerformanceRecordCard(
                title = "Worst Day",
                record = it,
                isPositive = false
            )
        }

        if (uiState.performanceInfo.bestDay == null && uiState.performanceInfo.worstDay == null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    DarkThemeColors.GlassBackground,
                                    DarkThemeColors.GlassHighlight
                                )
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Insufficient data for performance records",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Streak Information",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StreakStatCard(
                label = "Best Streak",
                value = "${uiState.performanceInfo.bestStreak}",
                type = uiState.performanceInfo.bestStreakType,
                modifier = Modifier.weight(1f)
            )
            StreakStatCard(
                label = "Worst Streak",
                value = "${uiState.performanceInfo.worstStreak}",
                type = uiState.performanceInfo.worstStreakType,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StreakStatCard(
                label = "Current Streak",
                value = "${uiState.performanceInfo.currentStreak}",
                type = uiState.performanceInfo.streakType,
                modifier = Modifier.weight(1f)
            )
            DetailCard(
                label = "Active Patterns",
                value = "${uiState.activePatterns.size}",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun SummaryCard(
    icon: ImageVector,
    label: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            DarkThemeColors.GlassBackground,
                            DarkThemeColors.GlassHighlight
                        )
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(14.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = valueColor,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = valueColor
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun DetailCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            DarkThemeColors.GlassBackground,
                            DarkThemeColors.GlassHighlight
                        )
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(14.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun PatternCard(pattern: PatternPerformance) {
    val accuracyColor = when {
        pattern.historicalAccuracy >= 60 -> DarkThemeColors.ChartGreen
        pattern.historicalAccuracy >= 40 -> DarkThemeColors.Warning
        else -> DarkThemeColors.ChartRed
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            DarkThemeColors.GlassBackground,
                            DarkThemeColors.GlassHighlight
                        )
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = pattern.patternName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = pattern.category,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "%.1f%%".format(pattern.confidence * 100),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = DarkThemeColors.Primary
                    )
                    Text(
                        text = "Confidence",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Detected: ${pattern.timesDetected}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Accuracy: %.1f%%".format(pattern.historicalAccuracy),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = accuracyColor
                )
            }
        }
    }
}

@Composable
private fun TrendDirectionCard(trendInfo: TrendInfo) {
    val directionIcon: ImageVector
    val directionColor: Color
    when (trendInfo.direction) {
        "Bullish" -> {
            directionIcon = Icons.Default.ArrowUpward
            directionColor = DarkThemeColors.ChartGreen
        }
        "Bearish" -> {
            directionIcon = Icons.Default.ArrowDownward
            directionColor = DarkThemeColors.ChartRed
        }
        else -> {
            directionIcon = Icons.Default.Info
            directionColor = DarkThemeColors.Warning
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            DarkThemeColors.GlassBackground,
                            DarkThemeColors.GlassHighlight
                        )
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = directionIcon,
                    contentDescription = null,
                    tint = directionColor,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${trendInfo.direction} Trend",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = directionColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Strength: %.1f%% | Slope: %.4f".format(
                        trendInfo.strength * 100, trendInfo.slope
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (trendInfo.isImproving) {
                        LabelBadge("Improving", DarkThemeColors.ChartGreen)
                    }
                    if (trendInfo.isDeclining) {
                        LabelBadge("Declining", DarkThemeColors.ChartRed)
                    }
                    if (!trendInfo.isImproving && !trendInfo.isDeclining) {
                        LabelBadge("Stable", DarkThemeColors.Warning)
                    }
                }
            }
        }
    }
}

@Composable
private fun TrendSignalBadge(text: String, positive: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = if (positive) listOf(
                            DarkThemeColors.ChartGreen.copy(alpha = 0.15f),
                            DarkThemeColors.ChartGreen.copy(alpha = 0.05f)
                        ) else listOf(
                            DarkThemeColors.ChartRed.copy(alpha = 0.15f),
                            DarkThemeColors.ChartRed.copy(alpha = 0.05f)
                        )
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = if (positive) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                    contentDescription = null,
                    tint = if (positive) DarkThemeColors.ChartGreen else DarkThemeColors.ChartRed,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = if (positive) DarkThemeColors.ChartGreen else DarkThemeColors.ChartRed
                )
            }
        }
    }
}

@Composable
private fun PerformanceRecordCard(title: String, record: PerformanceRecord, isPositive: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            if (isPositive) DarkThemeColors.ChartGreen.copy(alpha = 0.08f)
                            else DarkThemeColors.ChartRed.copy(alpha = 0.08f),
                            DarkThemeColors.GlassHighlight
                        )
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = record.label,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "%.2f".format(record.value),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (isPositive) DarkThemeColors.ChartGreen else DarkThemeColors.ChartRed
                        )
                    }
                    Icon(
                        imageVector = if (isPositive) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                        contentDescription = null,
                        tint = if (isPositive) DarkThemeColors.ChartGreen else DarkThemeColors.ChartRed,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun StreakStatCard(
    label: String,
    value: String,
    type: String,
    modifier: Modifier = Modifier
) {
    val streakColor = when {
        type.contains("Winning", ignoreCase = true) -> DarkThemeColors.ChartGreen
        type.contains("Losing", ignoreCase = true) -> DarkThemeColors.ChartRed
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            DarkThemeColors.GlassBackground,
                            DarkThemeColors.GlassHighlight
                        )
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(14.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = streakColor
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (type.isNotBlank() && type != "None") {
                    Text(
                        text = type,
                        style = MaterialTheme.typography.labelSmall,
                        color = streakColor.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
private fun LabelBadge(text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            color = color
        )
    }
}

@Composable
private fun CircularProgressIndicator(
    progress: Float,
    color: Color,
    trackColor: Color,
    strokeWidth: androidx.compose.ui.unit.Dp
) {
    val density = androidx.compose.ui.platform.LocalDensity.current
    val strokeWidthPx = with(density) { strokeWidth.toPx() }
    Box(modifier = Modifier.size(140.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val sweepAngle = progress * 360f
            drawCircle(
                brush = Brush.sweepGradient(
                    colors = listOf(trackColor, trackColor),
                    center = center
                ),
                radius = size.minDimension / 2 - strokeWidthPx / 2,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidthPx)
            )
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(color, color.copy(alpha = 0.5f)),
                    center = center
                ),
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidthPx)
            )
        }
    }
}

@Composable
private fun AnalyticsSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        repeat(5) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(DarkThemeColors.GlassBackground)
            )
        }
    }
}
