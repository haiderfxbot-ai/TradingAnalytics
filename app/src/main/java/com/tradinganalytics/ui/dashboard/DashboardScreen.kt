package com.tradinganalytics.ui.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tradinganalytics.core.theme.DarkThemeColors
import com.tradinganalytics.core.utils.formatCurrency
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DashboardScreen(
    uiState: DashboardUiState,
    onRefresh: () -> Unit
) {
    val pullRefreshState = rememberPullRefreshState(
        refreshing = uiState.isRefreshing,
        onRefresh = onRefresh
    )

    var showContent by remember { mutableStateOf(false) }
    LaunchedEffect(!uiState.isLoading) {
        if (!uiState.isLoading) {
            delay(100)
            showContent = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .pullRefresh(pullRefreshState)
    ) {
        if (uiState.isLoading && !uiState.isRefreshing) {
            DashboardSkeleton()
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AnimatedVisibility(
                    visible = showContent,
                    enter = fadeIn(animationSpec = tween(400)) +
                        slideInVertically(animationSpec = tween(400)) { it / 4 }
                ) {
                    GreetingSection(
                        greeting = uiState.greeting,
                        username = uiState.username
                    )
                }

                AnimatedVisibility(
                    visible = showContent,
                    enter = fadeIn(animationSpec = tween(500, delayMillis = 100))
                ) {
                    BalanceCard(
                        balance = uiState.currentBalance,
                        dailyPnL = uiState.dailyPnL,
                        isPositive = uiState.dailyPnL >= 0
                    )
                }

                AnimatedVisibility(
                    visible = showContent,
                    enter = fadeIn(animationSpec = tween(500, delayMillis = 200))
                ) {
                    QuickStatsRow(
                        dailyPnL = uiState.dailyPnL,
                        winRate = uiState.successRate,
                        currentStreak = maxOf(uiState.winStreak, uiState.lossStreak),
                        isStreakPositive = uiState.winStreak >= uiState.lossStreak
                    )
                }

                AnimatedVisibility(
                    visible = showContent,
                    enter = fadeIn(animationSpec = tween(500, delayMillis = 300))
                ) {
                    GoalProgressCard(
                        currentAmount = uiState.goalProgress.currentAmount,
                        targetAmount = uiState.goalProgress.targetAmount,
                        percentage = uiState.goalProgress.percentage,
                        isCompleted = uiState.goalProgress.isCompleted
                    )
                }

                AnimatedVisibility(
                    visible = showContent,
                    enter = fadeIn(animationSpec = tween(500, delayMillis = 400))
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        RiskLevelCard(
                            level = uiState.riskLevel,
                            score = uiState.riskScore,
                            modifier = Modifier.weight(1f)
                        )
                        ActivePatternCard(
                            patternName = uiState.activePattern,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                AnimatedVisibility(
                    visible = showContent,
                    enter = fadeIn(animationSpec = tween(500, delayMillis = 500))
                ) {
                    WinLossSummaryCard(
                        wins = uiState.winCount,
                        losses = uiState.lossCount,
                        successRate = uiState.successRate
                    )
                }

                AnimatedVisibility(
                    visible = showContent,
                    enter = fadeIn(animationSpec = tween(500, delayMillis = 600))
                ) {
                    StreakCard(
                        winStreak = uiState.winStreak,
                        lossStreak = uiState.lossStreak
                    )
                }

                AnimatedVisibility(
                    visible = showContent,
                    enter = fadeIn(animationSpec = tween(500, delayMillis = 700))
                ) {
                    LastActivityCard(
                        lastActivity = uiState.lastActivity,
                        activePattern = uiState.activePattern
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        PullRefreshIndicator(
            refreshing = uiState.isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter),
            backgroundColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun GreetingSection(greeting: String, username: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = "$greeting,",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = username,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun BalanceCard(balance: Double, dailyPnL: Double, isPositive: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            DarkThemeColors.Primary.copy(alpha = 0.15f),
                            DarkThemeColors.Primary.copy(alpha = 0.05f)
                        )
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                DarkThemeColors.GlassBackground,
                                DarkThemeColors.GlassHighlight
                            )
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
            )
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(DarkThemeColors.Primary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Wallet,
                            contentDescription = null,
                            tint = DarkThemeColors.Primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Text(
                        text = "Current Balance",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = balance.formatCurrency(showSymbol = true),
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = if (isPositive) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                        contentDescription = null,
                        tint = if (isPositive) DarkThemeColors.ChartGreen else DarkThemeColors.ChartRed,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${if (isPositive) "+" else ""}${dailyPnL.formatCurrency(showSymbol = true)} Today",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isPositive) DarkThemeColors.ChartGreen else DarkThemeColors.ChartRed,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickStatsRow(
    dailyPnL: Double,
    winRate: Double,
    currentStreak: Int,
    isStreakPositive: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            label = "Daily P&L",
            value = "${if (dailyPnL >= 0) "+" else ""}${dailyPnL.formatCurrency(showSymbol = false)}",
            valueColor = if (dailyPnL >= 0) DarkThemeColors.ChartGreen else DarkThemeColors.ChartRed,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            label = "Win Rate",
            value = "%.1f%%".format(winRate),
            valueColor = DarkThemeColors.Primary,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            label = "Streak",
            value = "$currentStreak",
            valueColor = if (isStreakPositive) DarkThemeColors.ChartGreen else DarkThemeColors.ChartRed,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatCard(
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
                .padding(12.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = valueColor
                )
            }
        }
    }
}

@Composable
private fun GoalProgressCard(
    currentAmount: Double,
    targetAmount: Double,
    percentage: Double,
    isCompleted: Boolean
) {
    val animProgress by animateFloatAsState(
        targetValue = (percentage / 100f).coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 1000, easing = LinearEasing)
    )

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
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Goal Progress",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (isCompleted) {
                        Text(
                            text = "Completed!",
                            style = MaterialTheme.typography.labelSmall,
                            color = DarkThemeColors.ChartGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animProgress)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = if (isCompleted) listOf(
                                        DarkThemeColors.ChartGreen,
                                        DarkThemeColors.Success
                                    ) else listOf(
                                        DarkThemeColors.Primary,
                                        DarkThemeColors.Secondary
                                    )
                                )
                            )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${currentAmount.formatCurrency(showSymbol = true)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "%.1f%%".format(percentage),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = DarkThemeColors.Primary
                    )
                    Text(
                        text = "${targetAmount.formatCurrency(showSymbol = true)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun RiskLevelCard(level: String, score: Double, modifier: Modifier = Modifier) {
    val riskColor = when (level) {
        "LOW" -> DarkThemeColors.ChartGreen
        "MEDIUM" -> DarkThemeColors.Warning
        "HIGH" -> DarkThemeColors.ChartRed
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
                    text = "Risk Level",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = level,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = riskColor
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "%.1f%%".format(score),
                    style = MaterialTheme.typography.labelSmall,
                    color = riskColor.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun ActivePatternCard(patternName: String?, modifier: Modifier = Modifier) {
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
                    text = "Active Pattern",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                Icon(
                    imageVector = Icons.Default.ShowChart,
                    contentDescription = null,
                    tint = DarkThemeColors.Secondary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = patternName ?: "None",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun WinLossSummaryCard(wins: Int, losses: Int, successRate: Double) {
    val total = wins + losses
    val winFraction = if (total > 0) wins.toFloat() / total else 0f

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
            Column {
                Text(
                    text = "Win / Loss Summary",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$wins",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = DarkThemeColors.ChartGreen
                        )
                        Text(
                            text = "Wins",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$losses",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = DarkThemeColors.ChartRed
                        )
                        Text(
                            text = "Losses",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$total",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Total",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(DarkThemeColors.ChartRed.copy(alpha = 0.3f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(winFraction)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(DarkThemeColors.ChartGreen)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "%.1f%% Success Rate".format(successRate),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}

@Composable
private fun StreakCard(winStreak: Int, lossStreak: Int) {
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
            Column {
                Text(
                    text = "Current Streaks",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = null,
                            tint = DarkThemeColors.ChartGreen,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$winStreak",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = DarkThemeColors.ChartGreen
                        )
                        Text(
                            text = "Win Streak",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.TrendingDown,
                            contentDescription = null,
                            tint = DarkThemeColors.ChartRed,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$lossStreak",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = DarkThemeColors.ChartRed
                        )
                        Text(
                            text = "Loss Streak",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LastActivityCard(lastActivity: Long?, activePattern: String?) {
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
            Column {
                Text(
                    text = "Last Activity",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (lastActivity != null) {
                    Text(
                        text = lastActivity.toRelativeTime(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                } else {
                    Text(
                        text = "No recent activity",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (activePattern != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Top pattern: $activePattern",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun DashboardSkeleton() {
    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.1f),
        Color.LightGray.copy(alpha = 0.2f),
        Color.LightGray.copy(alpha = 0.1f)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        repeat(6) {
            ShimmerBlock(
                height = when (it) {
                    0 -> 80
                    1 -> 120
                    2 -> 60
                    3 -> 100
                    4 -> 100
                    5 -> 80
                    else -> 60
                }.dp,
                shimmerColors = shimmerColors
            )
        }
    }
}

@Composable
private fun ShimmerBlock(height: androidx.compose.ui.unit.Dp, shimmerColors: List<Color>) {
    val shimmerTranslate = remember { androidx.compose.animation.core.Animatable(0f) }

    LaunchedEffect(Unit) {
        shimmerTranslate.animateTo(
            targetValue = 1f,
            animationSpec = androidx.compose.animation.core.repeatable(
                animation = tween(durationMillis = 1200, easing = LinearEasing),
                iterations = Int.MAX_VALUE,
                repeatMode = androidx.compose.animation.core.RepeatMode.Restart
            )
        )
    }

    val density = LocalDensity.current
    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(
            x = shimmerTranslate.value * with(density) { 400.dp.toPx() } - with(density) { 200.dp.toPx() },
            y = 0f
        ),
        end = Offset(
            x = shimmerTranslate.value * with(density) { 400.dp.toPx() } + with(density) { 200.dp.toPx() },
            y = 0f
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(16.dp))
            .background(brush)
    )
}

private fun Long.toRelativeTime(): String {
    val now = System.currentTimeMillis()
    val diff = now - this
    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24
    return when {
        seconds < 60 -> "Just now"
        minutes < 60 -> "${minutes}m ago"
        hours < 24 -> "${hours}h ago"
        days < 7 -> "${days}d ago"
        days < 30 -> "${days / 7}w ago"
        days < 365 -> "${days / 30}mo ago"
        else -> "${days / 365}y ago"
    }
}
