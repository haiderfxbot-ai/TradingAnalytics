package com.tradinganalytics.core.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object DarkThemeColors {
    val PremiumBackground = Color(0xFF0D1117)
    val SurfaceDark = Color(0xFF161B22)
    val SurfaceVariant = Color(0xFF1C2333)
    val CardDark = Color(0xFF1C2128)
    val CardElevated = Color(0xFF242933)

    val Primary = Color(0xFF7C4DFF)
    val PrimaryVariant = Color(0xFF651FFF)
    val PrimaryContainer = Color(0xFF2D1B69)
    val OnPrimaryContainer = Color(0xFFE8DEFF)

    val Secondary = Color(0xFF00E5FF)
    val SecondaryVariant = Color(0xFF00B8D4)
    val SecondaryContainer = Color(0xFF004D5C)
    val OnSecondaryContainer = Color(0xFFC4F0FF)

    val Tertiary = Color(0xFFFF6D00)
    val TertiaryContainer = Color(0xFF5C2100)
    val OnTertiaryContainer = Color(0xFFFFDCC2)

    val Error = Color(0xFFFF5252)
    val ErrorContainer = Color(0xFF5C1010)
    val OnErrorContainer = Color(0xFFFFDAD4)

    val OnBackground = Color(0xFFE6EDF3)
    val OnSurface = Color(0xFFE6EDF3)
    val OnSurfaceVariant = Color(0xFF8B949E)
    val Outline = Color(0xFF30363D)
    val OutlineVariant = Color(0xFF21262D)

    val Success = Color(0xFF00E676)
    val Warning = Color(0xFFFFAB40)
    val Info = Color(0xFF40C4FF)

    val GlassBackground = Color(0x1AFFFFFF)
    val GlassBorder = Color(0x33FFFFFF)
    val GlassHighlight = Color(0x0DFFFFFF)

    val ChartGreen = Color(0xFF00C853)
    val ChartRed = Color(0xFFFF1744)
    val ChartBlue = Color(0xFF2979FF)
    val ChartYellow = Color(0xFFFFD740)
    val ChartPurple = Color(0xFFD500F9)
    val ChartOrange = Color(0xFFFF9100)
    val ChartCyan = Color(0xFF00E5FF)
    val ChartPink = Color(0xFFFF4081)
}

object LightThemeColors {
    val PremiumBackground = Color(0xFFF5F7FA)
    val SurfaceLight = Color(0xFFFFFFFF)
    val SurfaceVariant = Color(0xFFF0F2F5)
    val CardLight = Color(0xFFFFFFFF)
    val CardElevated = Color(0xFFFFFFFF)

    val Primary = Color(0xFF7C4DFF)
    val PrimaryVariant = Color(0xFF651FFF)
    val PrimaryContainer = Color(0xFFEDE7FF)
    val OnPrimaryContainer = Color(0xFF1A004D)

    val Secondary = Color(0xFF00897B)
    val SecondaryVariant = Color(0xFF00695C)
    val SecondaryContainer = Color(0xFFB2DFDB)
    val OnSecondaryContainer = Color(0xFF00201C)

    val Tertiary = Color(0xFFEF6C00)
    val TertiaryContainer = Color(0xFFFFF3E0)
    val OnTertiaryContainer = Color(0xFF3E1A00)

    val Error = Color(0xFFD32F2F)
    val ErrorContainer = Color(0xFFFFCDD2)
    val OnErrorContainer = Color(0xFF410002)

    val OnBackground = Color(0xFF1A1C1E)
    val OnSurface = Color(0xFF1A1C1E)
    val OnSurfaceVariant = Color(0xFF5F6368)
    val Outline = Color(0xFFDADCE0)
    val OutlineVariant = Color(0xFFE8EAED)

    val Success = Color(0xFF2E7D32)
    val Warning = Color(0xFFF57C00)
    val Info = Color(0xFF0277BD)

    val GlassBackground = Color(0x0D000000)
    val GlassBorder = Color(0x1A000000)
    val GlassHighlight = Color(0x08000000)

    val ChartGreen = Color(0xFF2E7D32)
    val ChartRed = Color(0xFFC62828)
    val ChartBlue = Color(0xFF1565C0)
    val ChartYellow = Color(0xFFF9A825)
    val ChartPurple = Color(0xFF7B1FA2)
    val ChartOrange = Color(0xFFE65100)
    val ChartCyan = Color(0xFF00838F)
    val ChartPink = Color(0xFFAD1457)
}

object GlassColors {
    fun glassBackground(alpha: Float = 0.1f) = Color(0x1AFFFFFF).copy(alpha = alpha)
    fun glassBorder(alpha: Float = 0.2f) = Color(0x33FFFFFF).copy(alpha = alpha)
    fun glassBlur(alpha: Float = 0.15f) = Color(0x26FFFFFF).copy(alpha = alpha)
    val glassGradientStart = Color(0x1AFFFFFF)
    val glassGradientEnd = Color(0x08FFFFFF)
}

object StatusColors {
    val successGradient = Brush.horizontalGradient(
        colors = listOf(Color(0xFF00E676), Color(0xFF00C853))
    )
    val warningGradient = Brush.horizontalGradient(
        colors = listOf(Color(0xFFFFAB40), Color(0xFFFF9100))
    )
    val errorGradient = Brush.horizontalGradient(
        colors = listOf(Color(0xFFFF5252), Color(0xFFFF1744))
    )
    val infoGradient = Brush.horizontalGradient(
        colors = listOf(Color(0xFF40C4FF), Color(0xFF2979FF))
    )
    val primaryGradient = Brush.horizontalGradient(
        colors = listOf(Color(0xFF7C4DFF), Color(0xFF651FFF))
    )
    val premiumGradient = Brush.horizontalGradient(
        colors = listOf(Color(0xFF7C4DFF), Color(0xFF00E5FF))
    )
    val warnGradient = Brush.horizontalGradient(
        colors = listOf(Color(0xFFFF6D00), Color(0xFFFFAB40))
    )
}

object GradientDefs {
    val backgroundDark = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0D1117),
            Color(0xFF131820),
            Color(0xFF161B22)
        )
    )

    val backgroundLight = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFF5F7FA),
            Color(0xFFF8F9FB),
            Color(0xFFFFFFFF)
        )
    )

    val cardDark = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF1C2128),
            Color(0xFF1A1F26)
        )
    )

    val glassPanel = Brush.verticalGradient(
        colors = listOf(
            Color(0x1AFFFFFF),
            Color(0x0DFFFFFF),
            Color(0x08FFFFFF)
        )
    )

    val buttonPrimary = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF7C4DFF),
            Color(0xFF9C6FFF)
        )
    )

    val buttonSecondary = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF00E5FF),
            Color(0xFF40E5FF)
        )
    )

    val profitPositive = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF00E676),
            Color(0xFF00C853)
        )
    )

    val lossNegative = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFFF5252),
            Color(0xFFFF1744)
        )
    )

    val chartGradientUp = Brush.verticalGradient(
        colors = listOf(
            Color(0x4D00E676),
            Color(0x0000E676)
        )
    )

    val chartGradientDown = Brush.verticalGradient(
        colors = listOf(
            Color(0x4DFF5252),
            Color(0x00FF5252)
        )
    )

    val primaryToSecondary = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF7C4DFF),
            Color(0xFF651FFF),
            Color(0xFF00E5FF)
        )
    )
}
