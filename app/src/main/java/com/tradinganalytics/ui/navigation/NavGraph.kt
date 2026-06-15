package com.tradinganalytics.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.tradinganalytics.ui.admin.AdminDashboardScreen
import com.tradinganalytics.ui.admin.AdminViewModel
import com.tradinganalytics.ui.admin.UserManagementScreen
import com.tradinganalytics.ui.analytics.AnalyticsScreen
import com.tradinganalytics.ui.analytics.AnalyticsViewModel
import com.tradinganalytics.ui.backup.BackupEvent
import com.tradinganalytics.ui.backup.BackupScreen
import com.tradinganalytics.ui.backup.BackupViewModel
import com.tradinganalytics.ui.dashboard.DashboardScreen
import com.tradinganalytics.ui.dashboard.DashboardViewModel
import com.tradinganalytics.ui.history.HistoryScreen
import com.tradinganalytics.ui.history.HistoryViewModel
import com.tradinganalytics.ui.login.LoginScreen
import com.tradinganalytics.ui.patternlib.PatternDetailScreen
import com.tradinganalytics.ui.patternlib.PatternLibEvent
import com.tradinganalytics.ui.patternlib.PatternLibScreen
import com.tradinganalytics.ui.patternlib.PatternLibViewModel
import com.tradinganalytics.ui.reports.ReportsScreen
import com.tradinganalytics.ui.settings.SettingsScreen
import com.tradinganalytics.ui.splash.SplashScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String,
    isAdmin: Boolean,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(350)
            ) + fadeIn(animationSpec = tween(350))
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { -it / 3 },
                animationSpec = tween(350)
            ) + fadeOut(animationSpec = tween(350))
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { -it / 3 },
                animationSpec = tween(350)
            ) + fadeIn(animationSpec = tween(350))
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(350)
            ) + fadeOut(animationSpec = tween(350))
        }
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                navController = navController,
                onSplashComplete = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Dashboard.route) {
            val viewModel: DashboardViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsState()
            DashboardScreen(
                uiState = state,
                onRefresh = { viewModel.refreshDashboard() }
            )
        }
        composable(Screen.Analytics.route) {
            val viewModel: AnalyticsViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsState()
            AnalyticsScreen(
                uiState = state,
                onTabSelected = { viewModel.selectTab(it) },
                onRefresh = { viewModel.refresh() }
            )
        }
        composable(Screen.History.route) {
            val viewModel: HistoryViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsState()
            HistoryScreen(
                uiState = state,
                onFilterChange = { viewModel.onEvent(it) },
                onSearchQueryChange = { viewModel.onSearchQueryChange(it) },
                onRefresh = { viewModel.refresh() },
                onLoadMore = { viewModel.loadMore() }
            )
        }
        composable(Screen.Reports.route) {
            ReportsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.AdminDashboard.route) {
            AdminDashboardScreen(
                onNavigateToUserManagement = {
                    navController.navigate(Screen.UserManagement.route)
                },
                onNavigateToBackupManagement = {
                    navController.navigate(Screen.BackupManagement.route)
                },
                onNavigateToSystemSettings = {
                    navController.navigate(Screen.Settings.route)
                },
                onNavigateToSecuritySettings = {
                    navController.navigate(Screen.Settings.route)
                },
                onNavigateToDataImportExport = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }
        composable(Screen.UserManagement.route) {
            UserManagementScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.PatternLibrary.route) {
            val viewModel: PatternLibViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsState()
            PatternLibScreen(
                uiState = state,
                onEvent = { event -> viewModel.onEvent(event) },
                onPatternClick = { patternId ->
                    navController.navigate(Screen.PatternDetail.createRoute(patternId.toInt()))
                }
            )
        }
        composable(
            route = Screen.PatternDetail.route,
            arguments = listOf(navArgument("patternId") { type = NavType.IntType })
        ) { backStackEntry ->
            val patternId = backStackEntry.arguments?.getInt("patternId") ?: 0
            val viewModel: PatternLibViewModel = hiltViewModel()
            val state by viewModel.detailState.collectAsState()
            PatternDetailScreen(
                uiState = state,
                onFavoriteToggle = { viewModel.onEvent(PatternLibEvent.ToggleFavorite(patternId.toString())) },
                onNoteChange = { viewModel.onEvent(PatternLibEvent.NoteChange(it)) },
                onAddNote = { viewModel.onEvent(PatternLibEvent.AddNote) },
                onAnalyzeClick = { viewModel.onEvent(PatternLibEvent.AnalyzePattern(patternId.toString())) },
                onShareClick = { },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.BackupManagement.route) {
            val viewModel: BackupViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsState()
            BackupScreen(
                uiState = state,
                onEvent = { event -> viewModel.onEvent(event) }
            )
        }
        composable(Screen.ReportsViewer.route) {
            ReportsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
