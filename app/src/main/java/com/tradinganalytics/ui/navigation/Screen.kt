package com.tradinganalytics.ui.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Login : Screen("login")
    data object Dashboard : Screen("dashboard")
    data object Analytics : Screen("analytics")
    data object Reports : Screen("reports")
    data object History : Screen("history")
    data object Settings : Screen("settings")
    data object AdminDashboard : Screen("admin_dashboard")
    data object UserManagement : Screen("user_management")
    data object PatternLibrary : Screen("pattern_library")
    data object PatternDetail : Screen("pattern_detail/{patternId}") {
        fun createRoute(patternId: Int) = "pattern_detail/$patternId"
    }
    data object BackupManagement : Screen("backup_management")
    data object ReportsViewer : Screen("reports_viewer")
}
