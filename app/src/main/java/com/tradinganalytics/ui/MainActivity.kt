package com.tradinganalytics.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.tradinganalytics.core.theme.ThemeMode
import com.tradinganalytics.core.theme.TradingAnalyticsTheme
import com.tradinganalytics.core.utils.SessionManager
import com.tradinganalytics.ui.navigation.NavGraph
import com.tradinganalytics.ui.navigation.Screen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val sessionManager = SessionManager(this)

        setContent {
            TradingAnalyticsTheme(themeMode = ThemeMode.SYSTEM) {
                val navController = rememberNavController()
                val isLoggedIn = remember { mutableStateOf(false) }
                val isAdmin = remember { mutableStateOf(false) }
                val isChecking = remember { mutableStateOf(true) }

                LaunchedEffect(Unit) {
                    val sessionValid = sessionManager.isSessionValid()
                    isLoggedIn.value = sessionValid
                    if (sessionValid) {
                        isAdmin.value = sessionManager.isAdmin()
                    }
                    isChecking.value = false
                }

                val startDestination = if (!isChecking.value && isLoggedIn.value) {
                    Screen.Dashboard.route
                } else {
                    Screen.Splash.route
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    if (!isChecking.value) {
                        NavGraph(
                            navController = navController,
                            startDestination = startDestination,
                            isAdmin = isAdmin.value,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        )
                    }
                }
            }
        }

        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
        }
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT
    }
}
