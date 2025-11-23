package com.gws.auto.mobile.android.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// Define the color scheme for the dark theme using the Sharp Neon palette
private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = OnPrimaryDark,
    primaryContainer = PrimaryContainerDark,
    onPrimaryContainer = OnPrimaryContainerDark,
    secondary = SecondaryDark,
    onSecondary = OnSecondaryDark,
    secondaryContainer = SecondaryContainerDark,
    onSecondaryContainer = OnSecondaryContainerDark,
    tertiary = TertiaryDark,
    onTertiary = OnTertiaryDark,
    tertiaryContainer = TertiaryContainerDark,
    onTertiaryContainer = OnTertiaryContainerDark,
    error = ErrorDark,
    onError = OnErrorDark,
    errorContainer = ErrorContainerDark,
    onErrorContainer = OnErrorContainerDark,
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    outline = OutlineDark,
    inverseOnSurface = InverseOnSurfaceDark,
    inverseSurface = InverseSurfaceDark,
    inversePrimary = InversePrimaryDark,
)

// Define the color scheme for the light theme using the Sharp Neon palette
private val LightColorScheme = lightColorScheme(
    primary = PrimaryLight,
    onPrimary = OnPrimaryLight,
    primaryContainer = PrimaryContainerLight,
    onPrimaryContainer = OnPrimaryContainerLight,
    secondary = SecondaryLight,
    onSecondary = OnSecondaryLight,
    secondaryContainer = SecondaryContainerLight,
    onSecondaryContainer = OnSecondaryContainerLight,
    tertiary = TertiaryLight,
    onTertiary = OnTertiaryLight,
    tertiaryContainer = TertiaryContainerLight,
    onTertiaryContainer = OnTertiaryContainerLight,
    error = ErrorLight,
    onError = OnErrorLight,
    errorContainer = ErrorContainerLight,
    onErrorContainer = OnErrorContainerLight,
    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    outline = OutlineLight,
    inverseOnSurface = InverseOnSurfaceLight,
    inverseSurface = InverseSurfaceLight,
    inversePrimary = InversePrimaryLight,
)

// Define the shapes for the theme, overriding all to be RectangleShape for a sharp design
val AppShapes = Shapes(
    small = RoundedCornerShape(0.dp),
    medium = RoundedCornerShape(0.dp),
    large = RoundedCornerShape(0.dp)
)

@Composable
fun GWSAutoForAndroidTheme(
    theme: String = "System",
    highlightColor: String = "default",
    content: @Composable () -> Unit
) {
    // Determine if the dark theme should be used based on system or user setting
    val useDarkTheme = when (theme) {
        "Light" -> false
        "Dark" -> true
        else -> isSystemInDarkTheme()
    }

    // Select the appropriate color scheme
    // Dynamic color is disabled to enforce the Sharp Neon design system.
    val baseColorScheme = if (useDarkTheme) DarkColorScheme else LightColorScheme
    
    // Apply highlight color override for primary color
    val colorScheme = when (highlightColor) {
        "forest" -> baseColorScheme.copy(
            primary = if (useDarkTheme) ForestPrimaryDark else ForestPrimaryLight,
            primaryContainer = if (useDarkTheme) ForestPrimaryDark else ForestPrimaryLight,
            secondary = if (useDarkTheme) ForestPrimaryDark else ForestPrimaryLight,
            secondaryContainer = if (useDarkTheme) ForestPrimaryDark else ForestPrimaryLight,
            tertiary = if (useDarkTheme) ForestPrimaryDark else ForestPrimaryLight,
            tertiaryContainer = if (useDarkTheme) ForestPrimaryDark else ForestPrimaryLight,
            outline = if (useDarkTheme) ForestPrimaryDark else ForestPrimaryLight
        )
        "ocean" -> baseColorScheme.copy(
            primary = if (useDarkTheme) OceanPrimaryDark else OceanPrimaryLight,
            primaryContainer = if (useDarkTheme) OceanPrimaryDark else OceanPrimaryLight,
            secondary = if (useDarkTheme) OceanPrimaryDark else OceanPrimaryLight,
            secondaryContainer = if (useDarkTheme) OceanPrimaryDark else OceanPrimaryLight,
            tertiary = if (useDarkTheme) OceanPrimaryDark else OceanPrimaryLight,
            tertiaryContainer = if (useDarkTheme) OceanPrimaryDark else OceanPrimaryLight,
            outline = if (useDarkTheme) OceanPrimaryDark else OceanPrimaryLight
        )
        "sakura" -> baseColorScheme.copy(
            primary = if (useDarkTheme) SakuraPrimaryDark else SakuraPrimaryLight,
            primaryContainer = if (useDarkTheme) SakuraPrimaryDark else SakuraPrimaryLight,
            secondary = if (useDarkTheme) SakuraPrimaryDark else SakuraPrimaryLight,
            secondaryContainer = if (useDarkTheme) SakuraPrimaryDark else SakuraPrimaryLight,
            tertiary = if (useDarkTheme) SakuraPrimaryDark else SakuraPrimaryLight,
            tertiaryContainer = if (useDarkTheme) SakuraPrimaryDark else SakuraPrimaryLight,
            outline = if (useDarkTheme) SakuraPrimaryDark else SakuraPrimaryLight
        )
        "neon" -> baseColorScheme.copy(
            primary = if (useDarkTheme) NeonPrimaryDark else NeonPrimaryLight,
            primaryContainer = if (useDarkTheme) NeonPrimaryDark else NeonPrimaryLight,
            secondary = if (useDarkTheme) NeonPrimaryDark else NeonPrimaryLight,
            secondaryContainer = if (useDarkTheme) NeonPrimaryDark else NeonPrimaryLight,
            tertiary = if (useDarkTheme) NeonPrimaryDark else NeonPrimaryLight,
            tertiaryContainer = if (useDarkTheme) NeonPrimaryDark else NeonPrimaryLight,
            outline = if (useDarkTheme) NeonPrimaryDark else NeonPrimaryLight
        )
        else -> baseColorScheme.copy( // "default" - Monochrome
            primary = if (useDarkTheme) MonochromePrimaryDark else MonochromePrimaryLight,
            primaryContainer = if (useDarkTheme) MonochromePrimaryDark else MonochromePrimaryLight,
            secondary = if (useDarkTheme) MonochromePrimaryDark else MonochromePrimaryLight,
            secondaryContainer = if (useDarkTheme) MonochromePrimaryDark else MonochromePrimaryLight,
            tertiary = if (useDarkTheme) MonochromePrimaryDark else MonochromePrimaryLight,
            tertiaryContainer = if (useDarkTheme) MonochromePrimaryDark else MonochromePrimaryLight,
            outline = if (useDarkTheme) MonochromePrimaryDark else MonochromePrimaryLight
        )
    }

    // PlatformRippleがIndicationNodeFactoryを実装していない問題を回避するため
    // Rippleを透明にするカスタムRippleThemeを定義
    val noRippleTheme = object : RippleTheme {
        @Composable
        override fun defaultColor(): Color = Color.Transparent
        
        @Composable
        override fun rippleAlpha(): RippleAlpha = RippleAlpha(0f, 0f, 0f, 0f)
    }

    // Apply the MaterialTheme with the new color scheme, typography, and shapes
    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        shapes = AppShapes // Apply the sharp shapes
    ) {
        // LocalRippleThemeでrippleを無効化してclickable修飾子の互換性エラーを回避
        CompositionLocalProvider(LocalRippleTheme provides noRippleTheme) {
            content()
        }
    }
}

