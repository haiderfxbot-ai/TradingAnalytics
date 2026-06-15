package com.tradinganalytics.ui.patternlib

@file:OptIn(ExperimentalMaterial3Api::class)




import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.BorderStroke

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tradinganalytics.core.theme.DarkThemeColors
import com.tradinganalytics.patterns.library.PatternDefinition
import com.tradinganalytics.ui.components.SkeletonCard

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PatternLibScreen(
    uiState: PatternLibUiState,
    onEvent: (PatternLibEvent) -> Unit,
    onPatternClick: (String) -> Unit
) {
    val pullRefreshState = rememberPullRefreshState(
        refreshing = uiState.isRefreshing,
        onRefresh = { onEvent(PatternLibEvent.Refresh) }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Pattern Library",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                actions = {
                    IconButton(onClick = { onEvent(PatternLibEvent.ToggleViewMode) }) {
                        Icon(
                            imageVector = if (uiState.viewMode == PatternViewMode.GRID)
                                Icons.Default.ViewList else Icons.Default.GridView,
                            contentDescription = "Toggle view",
                            tint = DarkThemeColors.OnSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { },
                containerColor = DarkThemeColors.Primary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Outlined.AutoAwesome, contentDescription = "Quick Analysis")
            }
        },
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .pullRefresh(pullRefreshState)
        ) {
            if (uiState.isLoading) {
                PatternLibSkeleton()
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    SearchBar(
                        query = uiState.searchQuery,
                        onQueryChange = { onEvent(PatternLibEvent.OnSearchQueryChange(it)) }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    FilterChipsRow(
                        selectedFilter = uiState.selectedFilter,
                        onFilterSelect = { onEvent(PatternLibEvent.OnFilterSelect(it)) }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${uiState.filteredPatterns.size} patterns",
                        style = MaterialTheme.typography.labelMedium,
                        color = DarkThemeColors.OnSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (uiState.filteredPatterns.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No patterns found",
                                style = MaterialTheme.typography.bodyLarge,
                                color = DarkThemeColors.OnSurfaceVariant
                            )
                        }
                    } else if (uiState.viewMode == PatternViewMode.GRID) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(
                                items = uiState.filteredPatterns,
                                key = { it.patternId }
                            ) { pattern ->
                                PatternGridCard(
                                    pattern = pattern,
                                    isFavorite = uiState.favoriteIds.contains(pattern.patternId),
                                    onClick = { onPatternClick(pattern.patternId) },
                                    onFavoriteToggle = { onEvent(PatternLibEvent.ToggleFavorite(pattern.patternId)) }
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(
                                items = uiState.filteredPatterns,
                                key = { it.patternId }
                            ) { pattern ->
                                PatternListItem(
                                    pattern = pattern,
                                    isFavorite = uiState.favoriteIds.contains(pattern.patternId),
                                    onClick = { onPatternClick(pattern.patternId) },
                                    onFavoriteToggle = { onEvent(PatternLibEvent.ToggleFavorite(pattern.patternId)) }
                                )
                            }
                        }
                    }
                }
            }

            PullRefreshIndicator(
                refreshing = uiState.isRefreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter),
                backgroundColor = DarkThemeColors.SurfaceDark,
                contentColor = DarkThemeColors.Primary
            )
        }
    }
}

@Composable
private fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = {
            Text("Search patterns...", color = DarkThemeColors.OnSurfaceVariant)
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = DarkThemeColors.OnSurfaceVariant
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Clear",
                        tint = DarkThemeColors.OnSurfaceVariant
                    )
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = DarkThemeColors.Primary.copy(alpha = 0.5f),
            unfocusedBorderColor = DarkThemeColors.GlassBorder,
            focusedContainerColor = DarkThemeColors.SurfaceVariant,
            unfocusedContainerColor = DarkThemeColors.SurfaceVariant,
            cursorColor = DarkThemeColors.Primary
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
    )
}

@Composable
private fun FilterChipsRow(
    selectedFilter: PatternFilter,
    onFilterSelect: (PatternFilter) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        PatternFilter.entries.forEach { filter ->
            FilterChip(
                selected = selectedFilter == filter,
                onClick = { onFilterSelect(filter) },
                label = {
                    Text(
                        text = filter.displayName,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (selectedFilter == filter) FontWeight.SemiBold else FontWeight.Normal
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = DarkThemeColors.Primary.copy(alpha = 0.2f),
                    selectedLabelColor = DarkThemeColors.Primary
                ),
                border = if (selectedFilter == filter) {
                    BorderStroke(1.dp, DarkThemeColors.Primary.copy(alpha = 0.5f))
                } else {
                    BorderStroke(1.dp, DarkThemeColors.GlassBorder)
                },
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}

@Composable
private fun PatternGridCard(
    pattern: PatternDefinition,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onFavoriteToggle: () -> Unit
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
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        DarkThemeColors.GlassBackground,
                        DarkThemeColors.GlassHighlight
                    )
                )
            )
            .border(
                width = 0.5.dp,
                color = DarkThemeColors.GlassBorder,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                CategoryBadge(category = pattern.category)
                IconButton(
                    onClick = onFavoriteToggle,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (isFavorite) "Remove favorite" else "Add favorite",
                        tint = if (isFavorite) DarkThemeColors.ChartRed else DarkThemeColors.OnSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = pattern.name,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = DarkThemeColors.OnSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(riskColor)
                )
                Text(
                    text = pattern.riskRating,
                    style = MaterialTheme.typography.labelSmall,
                    color = riskColor,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun PatternListItem(
    pattern: PatternDefinition,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onFavoriteToggle: () -> Unit
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
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        DarkThemeColors.GlassBackground,
                        DarkThemeColors.GlassHighlight
                    )
                )
            )
            .border(
                width = 0.5.dp,
                color = DarkThemeColors.GlassBorder,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CategoryBadge(category = pattern.category)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(riskColor)
                        )
                        Text(
                            text = pattern.riskRating,
                            style = MaterialTheme.typography.labelSmall,
                            color = riskColor,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = pattern.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkThemeColors.OnSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = pattern.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = DarkThemeColors.OnSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(onClick = onFavoriteToggle) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (isFavorite) "Remove favorite" else "Add favorite",
                    tint = if (isFavorite) DarkThemeColors.ChartRed else DarkThemeColors.OnSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun CategoryBadge(category: String) {
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
            .clip(RoundedCornerShape(6.dp))
            .background(badgeColor.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = category.split(" ").first(),
            style = MaterialTheme.typography.labelSmall,
            color = badgeColor,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun PatternLibSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SkeletonCard(height = 48.dp)
        Spacer(modifier = Modifier.height(4.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(4) {
                SkeletonCard(height = 32.dp, modifier = Modifier.width(80.dp))
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        repeat(6) { idx ->
            SkeletonCard(
                height = if (idx % 2 == 0) 160.dp else 120.dp,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

