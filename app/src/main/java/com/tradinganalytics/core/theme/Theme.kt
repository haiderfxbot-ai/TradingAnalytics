package com.tradinganalytics.core.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = DarkThemeColors.Primary,
    onPrimary = Color.White,
    primaryContainer = DarkThemeColors.PrimaryContainer,
    onPrimaryContainer = DarkThemeColors.OnPrimaryContainer,
    secondary = DarkThemeColors.Secondary,
    onSecondary = Color.Black,
    secondaryContainer = DarkThemeColors.SecondaryContainer,
    onSecondaryContainer = DarkThemeColors.OnSecondaryContainer,
    tertiary = DarkThemeColors.Tertiary,
    onTertiary = Color.Black,
    tertiaryContainer = DarkThemeColors.TertiaryContainer,
    onTertiaryContainer = DarkThemeColors.OnTertiaryContainer,
    error = DarkThemeColors.Error,
    onError = Color.Black,
    errorContainer = DarkThemeColors.ErrorContainer,
    onErrorContainer = DarkThemeColors.OnErrorContainer,
    background = DarkThemeColors.PremiumBackground,
    onBackground = DarkThemeColors.OnBackground,
    surface = DarkThemeColors.SurfaceDark,
    onSurface = DarkThemeColors.OnSurface,
    surfaceVariant = DarkThemeColors.SurfaceVariant,
    onSurfaceVariant = DarkThemeColors.OnSurfaceVariant,
    outline = DarkThemeColors.Outline,
    outlineVariant = DarkThemeColors.OutlineVariant,
    inverseSurface = Color(0xFFE6EDF3),
    inverseOnSurface = Color(0xFF0D1117),
    inversePrimary = DarkThemeColors.PrimaryVariant,
    surfaceTint = DarkThemeColors.Primary
)

private val LightColorScheme = lightColorScheme(
    primary = LightThemeColors.Primary,
    onPrimary = Color.White,
    primaryContainer = LightThemeColors.PrimaryContainer,
    onPrimaryContainer = LightThemeColors.OnPrimaryContainer,
    secondary = LightThemeColors.Secondary,
    onSecondary = Color.White,
    secondaryContainer = LightThemeColors.SecondaryContainer,
    onSecondaryContainer = LightThemeColors.OnSecondaryContainer,
    tertiary = LightThemeColors.Tertiary,
    onTertiary = Color.White,
    tertiaryContainer = LightThemeColors.TertiaryContainer,
    onTertiaryContainer = LightThemeColors.OnTertiaryContainer,
    error = LightThemeColors.Error,
    onError = Color.White,
    errorContainer = LightThemeColors.ErrorContainer,
    onErrorContainer = LightThemeColors.OnErrorContainer,
    background = LightThemeColors.PremiumBackground,
    onBackground = LightThemeColors.OnBackground,
    surface = LightThemeColors.SurfaceLight,
    onSurface = LightThemeColors.OnSurface,
    surfaceVariant = LightThemeColors.SurfaceVariant,
    onSurfaceVariant = LightThemeColors.OnSurfaceVariant,
    outline = LightThemeColors.Outline,
    outlineVariant = LightThemeColors.OutlineVariant,
    inverseSurface = Color(0xFF1A1C1E),
    inverseOnSurface = Color(0xFFF5F7FA),
    inversePrimary = LightThemeColors.Primary,
    surfaceTint = LightThemeColors.Primary
)

private val AppTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp
    ),
    displaySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 26.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.5.sp
    )
)

private val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

enum class ThemeMode {
    SYSTEM, LIGHT, DARK
}

@Composable
fun TradingAnalyticsTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val systemDark = isSystemInDarkTheme()

    val useDarkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> systemDark
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (useDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        useDarkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !useDarkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}
