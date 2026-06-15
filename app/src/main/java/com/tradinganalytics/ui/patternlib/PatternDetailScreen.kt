package com.tradinganalytics.ui.patternlib

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tradinganalytics.core.theme.DarkThemeColors
import com.tradinganalytics.data.database.entities.PatternMatchEntity
import com.tradinganalytics.patterns.library.PatternDefinition
import com.tradinganalytics.ui.components.SkeletonCard

@Composable
fun PatternDetailScreen(
    uiState: PatternDetailUiState,
    onFavoriteToggle: () -> Unit,
    onNoteChange: (String) -> Unit,
    onAddNote: () -> Unit,
    onAnalyzeClick: () -> Unit,
    onShareClick: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (uiState.isLoading) {
            PatternDetailSkeleton()
        } else {
            val pattern = uiState.pattern
            if (pattern == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Pattern not found", color = DarkThemeColors.OnSurfaceVariant)
                }
                return
            }

            PatternHeader(
                pattern = pattern,
                isFavorite = uiState.isFavorite,
                onFavoriteToggle = onFavoriteToggle,
                onBack = onBack
            )

            DescriptionSection(pattern.description)

            DetectionRulesSection(pattern)

            HistoricalPerformanceSection(pattern)

            MatchHistorySection(uiState.matchHistory)

            NotesSection(
                notes = uiState.notes,
                noteInput = uiState.noteInput,
                onNoteChange = onNoteChange,
                onAddNote = onAddNote
            )

            ActionButtons(
                onAnalyzeClick = onAnalyzeClick,
                onShareClick = onShareClick,
                isAnalyzing = uiState.isAnalyzing
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun PatternHeader(
    pattern: PatternDefinition,
    isFavorite: Boolean,
    onFavoriteToggle: () -> Unit,
    onBack: () -> Unit
) {
    val riskColor = when (pattern.riskRating.lowercase()) {
        "low" -> DarkThemeColors.ChartGreen
        "medium" -> DarkThemeColors.ChartYellow
        "high" -> DarkThemeColors.ChartOrange
        "very high" -> DarkThemeColors.ChartRed
        else -> DarkThemeColors.OnSurfaceVariant
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        DarkThemeColors.GlassBackground,
                        DarkThemeColors.GlassHighlight
                    )
                )
            )
            .border(0.5.dp, DarkThemeColors.GlassBorder, RoundedCornerShape(20.dp))
            .padding(20.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CategoryLabel(pattern.category)
                    RiskIndicator(riskColor, pattern.riskRating)
                }
                Row {
                    IconButton(onClick = onFavoriteToggle) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Toggle favorite",
                            tint = if (isFavorite) DarkThemeColors.ChartRed else DarkThemeColors.OnSurfaceVariant,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = pattern.name.replace("_", " "),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = DarkThemeColors.OnSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "ID: ${pattern.patternId}",
                style = MaterialTheme.typography.labelSmall,
                color = DarkThemeColors.OnSurfaceVariant
            )
        }
    }
}

@Composable
private fun CategoryLabel(category: String) {
    val badgeColor = when {
        category.contains("Trend", ignoreCase = true) -> DarkThemeColors.ChartBlue
        category.contains("Reversal", ignoreCase = true) -> DarkThemeColors.ChartPurple
        category.contains("Repeating", ignoreCase = true) -> DarkThemeColors.ChartGreen
        category.contains("Frequency", ignoreCase = true) -> DarkThemeColors.ChartOrange
        category.contains("Timing", ignoreCase = true) -> DarkThemeColors.ChartCyan
        category.contains("Probability", ignoreCase = true) -> DarkThemeColors.ChartPink
        category.contains("Momentum", ignoreCase = true) -> DarkThemeColors.ChartYellow
        category.contains("Statistical", ignoreCase = true) -> DarkThemeColors.ChartRed
        else -> DarkThemeColors.OnSurfaceVariant
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(badgeColor.copy(alpha = 0.15f))
            .padding(horizontal = 12.dp, vertical = 5.dp)
    ) {
        Text(
            text = category,
            style = MaterialTheme.typography.labelMedium,
            color = badgeColor,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun RiskIndicator(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = color,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun DescriptionSection(description: String) {
    SectionCard(title = "Description") {
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = DarkThemeColors.OnSurface
        )
    }
}

@Composable
private fun DetectionRulesSection(pattern: PatternDefinition) {
    SectionCard(title = "Detection Rules") {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            InfoRow("Min Sequence Length", "${pattern.detectionRules.minSequenceLength}")
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Required Conditions",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = DarkThemeColors.OnSurfaceVariant
            )
            pattern.detectionRules.requiredConditions.forEachIndexed { index, condition ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(DarkThemeColors.Primary)
                    )
                    Text(
                        text = condition.replace("_", " "),
                        style = MaterialTheme.typography.bodySmall,
                        color = DarkThemeColors.OnSurface
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Confidence Formula",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = DarkThemeColors.OnSurfaceVariant
            )
            Text(
                text = pattern.confidenceFormula,
                style = MaterialTheme.typography.bodySmall,
                color = DarkThemeColors.Secondary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun HistoricalPerformanceSection(pattern: PatternDefinition) {
    SectionCard(title = "Historical Performance") {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            PerformanceBar(
                label = "Confidence",
                value = 0.75,
                color = DarkThemeColors.Primary
            )
            PerformanceBar(
                label = "Similarity",
                value = 0.62,
                color = DarkThemeColors.Secondary
            )
            PerformanceBar(
                label = "Success Rate",
                value = 0.68,
                color = DarkThemeColors.ChartGreen
            )
            PerformanceBar(
                label = "Risk Score",
                value = when (pattern.riskRating.lowercase()) {
                    "low" -> 0.2
                    "medium" -> 0.5
                    "high" -> 0.75
                    "very high" -> 0.9
                    else -> 0.5
                },
                color = when (pattern.riskRating.lowercase()) {
                    "low" -> DarkThemeColors.ChartGreen
                    "medium" -> DarkThemeColors.ChartYellow
                    "high" -> DarkThemeColors.ChartOrange
                    "very high" -> DarkThemeColors.ChartRed
                    else -> DarkThemeColors.OnSurfaceVariant
                }
            )
        }
    }
}

@Composable
private fun PerformanceBar(label: String, value: Double, color: Color) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = DarkThemeColors.OnSurfaceVariant
            )
            Text(
                text = "${(value * 100).toInt()}%",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = color
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(DarkThemeColors.SurfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(value.toFloat().coerceIn(0f, 1f))
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(color.copy(alpha = 0.7f), color)
                        )
                    )
            )
        }
    }
}

@Composable
private fun MatchHistorySection(matches: List<PatternMatchEntity>) {
    SectionCard(title = "Match History") {
        if (matches.isEmpty()) {
            Text(
                text = "No match history available",
                style = MaterialTheme.typography.bodySmall,
                color = DarkThemeColors.OnSurfaceVariant
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                matches.take(10).forEach { match ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(DarkThemeColors.SurfaceVariant.copy(alpha = 0.5f))
                            .padding(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Score: ${"%.2f".format(match.similarityScore)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium,
                                    color = DarkThemeColors.OnSurface
                                )
                                Text(
                                    text = match.confidenceLevel,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = DarkThemeColors.OnSurfaceVariant
                                )
                            }
                            Text(
                                text = match.matchedDate.toLocaleString(),
                                style = MaterialTheme.typography.labelSmall,
                                color = DarkThemeColors.OnSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NotesSection(
    notes: List<String>,
    noteInput: String,
    onNoteChange: (String) -> Unit,
    onAddNote: () -> Unit
) {
    SectionCard(title = "Notes") {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = noteInput,
                    onValueChange = onNoteChange,
                    placeholder = {
                        Text("Add a note...", color = DarkThemeColors.OnSurfaceVariant)
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DarkThemeColors.Primary.copy(alpha = 0.5f),
                        unfocusedBorderColor = DarkThemeColors.GlassBorder,
                        focusedContainerColor = DarkThemeColors.SurfaceVariant,
                        unfocusedContainerColor = DarkThemeColors.SurfaceVariant,
                        cursorColor = DarkThemeColors.Primary
                    ),
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = onAddNote,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkThemeColors.Primary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    enabled = noteInput.isNotBlank()
                ) {
                    Text("Add")
                }
            }
            if (notes.isEmpty()) {
                Text(
                    text = "No notes yet",
                    style = MaterialTheme.typography.bodySmall,
                    color = DarkThemeColors.OnSurfaceVariant
                )
            } else {
                notes.forEach { note ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(DarkThemeColors.SurfaceVariant.copy(alpha = 0.5f))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = note,
                            style = MaterialTheme.typography.bodySmall,
                            color = DarkThemeColors.OnSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionButtons(
    onAnalyzeClick: () -> Unit,
    onShareClick: () -> Unit,
    isAnalyzing: Boolean
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Button(
            onClick = onAnalyzeClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isAnalyzing,
            colors = ButtonDefaults.buttonColors(
                containerColor = DarkThemeColors.Primary
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ShowChart,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isAnalyzing) "Analyzing..." else "Analyze with this Pattern",
                fontWeight = FontWeight.SemiBold
            )
        }

        OutlinedButton(
            onClick = onShareClick,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = DarkThemeColors.Secondary),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkThemeColors.Secondary.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Share Pattern Info", fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        DarkThemeColors.GlassBackground,
                        DarkThemeColors.GlassHighlight
                    )
                )
            )
            .border(0.5.dp, DarkThemeColors.GlassBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = DarkThemeColors.OnSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = DarkThemeColors.OnSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = DarkThemeColors.OnSurface
        )
    }
}

@Composable
private fun PatternDetailSkeleton() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SkeletonCard(height = 140.dp)
        SkeletonCard(height = 100.dp)
        SkeletonCard(height = 180.dp)
        SkeletonCard(height = 200.dp)
        SkeletonCard(height = 120.dp)
    }
}
