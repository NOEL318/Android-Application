package com.example.application.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// darkmode
private val DarkColorScheme = darkColorScheme(
    primary = BbvaBlue,
    onPrimary = BbvaWhite,
    secondary = BbvaAqua,
    onSecondary = BbvaNavy,
    background = BbvaNavy,
    surface = BbvaNavy,
    onBackground = BbvaWhite,
    onSurface = BbvaWhite
)

// white pallete
private val LightColorScheme = lightColorScheme(
    primary = BbvaBlue,
    onPrimary = BbvaWhite,
    secondary = BbvaNavy,
    onSecondary = BbvaWhite,
    tertiary = BbvaAqua,
    background = BbvaGray,
    surface = BbvaWhite,
    onBackground = BbvaTextBlack,
    onSurface = BbvaTextBlack
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // force my colors and not ui system android default
    dynamicColor: Boolean = false, 
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // status bar when use it
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // blue bar
            window.statusBarColor = BbvaNavy.toArgb() 
            // icons in white
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}