package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = FarmGreenPrimaryDark,
    onPrimary = Color(0xFF052E16),
    primaryContainer = FarmGreenContainerDark,
    onPrimaryContainer = Color(0xFF86EFAC),
    secondary = HarvestAmberDark,
    onSecondary = Color(0xFF451A03),
    secondaryContainer = HarvestAmberContainerDark,
    onSecondaryContainer = Color(0xFFFDE68A),
    tertiary = LeafTealDark,
    background = FreshBgDark,
    onBackground = TextPrimaryDark,
    surface = FreshSurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = FreshSurfaceVariantDark,
    onSurfaceVariant = TextSecondaryDark
)

private val LightColorScheme = lightColorScheme(
    primary = FarmGreenPrimary,
    onPrimary = Color.White,
    primaryContainer = FarmGreenContainer,
    onPrimaryContainer = OnFarmGreenContainer,
    secondary = HarvestAmber,
    onSecondary = Color.White,
    secondaryContainer = HarvestAmberContainer,
    onSecondaryContainer = Color(0xFF78350F),
    tertiary = LeafTeal,
    background = FreshBgLight,
    onBackground = TextPrimaryLight,
    surface = FreshSurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = FreshSurfaceVariantLight,
    onSurfaceVariant = TextSecondaryLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep farm branding colors consistent
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
