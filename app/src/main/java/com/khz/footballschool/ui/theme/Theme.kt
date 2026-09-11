package com.khz.footballschool.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = PurplePrimary,
    onPrimary = PurpleOnPrimary,
    primaryContainer = PurpleMedium,
    onPrimaryContainer = GoldLight,

    secondary = GoldPrimary,
    onSecondary = GoldOnSecondary,
    secondaryContainer = GoldDark,
    onSecondaryContainer = PurpleDark,

    tertiary = BlueAccent,
    onTertiary = BlueOnTertiary,
    tertiaryContainer = BlueLight,
    onTertiaryContainer = Color.White,

    background = PurpleDark,
    onBackground = TextPrimary,

    surface = PurpleMedium,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = TextSecondary,

    error = RedError,
    onError = Color.White,
    errorContainer = RedLight,
    onErrorContainer = Color.White,

    outline = OutlineColor,
    outlineVariant = PurpleLight,
)

private val LightColorScheme = lightColorScheme(
    primary = PurplePrimary,
    onPrimary = Color.White,
    primaryContainer = PurpleLight,
    onPrimaryContainer = PurpleDark,

    secondary = GoldPrimary,
    onSecondary = PurpleDark,
    secondaryContainer = GoldLight,
    onSecondaryContainer = PurpleDark,

    tertiary = BlueAccent,
    onTertiary = Color.White,
    tertiaryContainer = BlueLight,
    onTertiaryContainer = Color.White,

    background = Color(0xFFF3E5F5),
    onBackground = PurpleDark,

    surface = Color.White,
    onSurface = PurpleDark,
    surfaceVariant = Color(0xFFEDE7F6),
    onSurfaceVariant = PurpleMedium,

    error = RedError,
    onError = Color.White,

    outline = PurpleLight,
    outlineVariant = PurplePrimary,
)

@Composable
fun FootballSchoolTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalView.current.context
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }

        darkTheme                                                      -> DarkColorScheme
        else                                                           -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = PurpleDark.toArgb()
            WindowCompat.getInsetsController(
                window,
                view
            ).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}