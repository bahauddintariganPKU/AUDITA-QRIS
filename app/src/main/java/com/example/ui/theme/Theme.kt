package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
  primary = PastelMintDark,
  onPrimary = DarkBg,
  primaryContainer = PastelMintPrimary,
  onPrimaryContainer = PastelMintLight,
  secondary = PastelLavender,
  onSecondary = DarkBg,
  secondaryContainer = DarkSurfaceVariant,
  onSecondaryContainer = PastelLavenderLight,
  tertiary = PastelPeach,
  background = DarkBg,
  surface = DarkSurface,
  surfaceVariant = DarkSurfaceVariant,
  onBackground = DarkTextPrimary,
  onSurface = DarkTextPrimary,
  onSurfaceVariant = DarkTextSecondary,
  outline = DarkOutline
)

private val LightColorScheme = lightColorScheme(
  primary = PastelMintPrimary,
  onPrimary = LightSurface,
  primaryContainer = PastelMintContainer,
  onPrimaryContainer = PastelMintPrimary,
  secondary = PastelLavender,
  onSecondary = LightSurface,
  secondaryContainer = PastelLavenderContainer,
  onSecondaryContainer = PastelLavender,
  tertiary = PastelPeach,
  background = LightBg,
  surface = LightSurface,
  surfaceVariant = LightSurfaceVariant,
  onBackground = LightTextPrimary,
  onSurface = LightTextPrimary,
  onSurfaceVariant = LightTextSecondary,
  outline = LightOutline
)

enum class AppThemeMode {
  AUTO, LIGHT, DARK
}

@Composable
fun MyApplicationTheme(
  themeMode: AppThemeMode = AppThemeMode.AUTO,
  dynamicColor: Boolean = false, // Use our handcrafted fresh pastel scheme by default
  content: @Composable () -> Unit
) {
  val isDark = when (themeMode) {
    AppThemeMode.AUTO -> {
      // Check system or night hours (19:00 - 06:00)
      val currentHour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
      isSystemInDarkTheme() || (currentHour >= 19 || currentHour < 6)
    }
    AppThemeMode.LIGHT -> false
    AppThemeMode.DARK -> true
  }

  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    isDark -> DarkColorScheme
    else -> LightColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
