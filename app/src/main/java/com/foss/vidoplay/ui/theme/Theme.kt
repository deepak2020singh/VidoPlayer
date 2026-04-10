package com.foss.vidoplay.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.foss.vidoplay.data.local.pref.ColorSchemes
import com.foss.vidoplay.data.local.pref.ThemeMode
import com.foss.vidoplay.data.local.pref.ThemePreferences

// ── DEFAULT ──────────────────────────────────────────────────────────────────
private val DefaultLight = lightColorScheme(
    primary = Color(0xFF1976D2),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFBBDEFB),
    onPrimaryContainer = Color(0xFF0D47A1),
    secondary = Color(0xFF00897B),
    onSecondary = Color.White,
    tertiary = Color(0xFF6A1B9A),
    onTertiary = Color.White,
    background = Color(0xFFF5F5F5),
    onBackground = Color(0xFF212121),
    surface = Color.White,
    onSurface = Color(0xFF212121),
    surfaceVariant = Color(0xFFE0E0E0),
    onSurfaceVariant = Color(0xFF757575),
    error = Color(0xFFB00020),
    onError = Color.White
)

private val DefaultDark = darkColorScheme(
    primary = Color(0xFF90CAF9),
    onPrimary = Color(0xFF0D47A1),
    primaryContainer = Color(0xFF1565C0),
    onPrimaryContainer = Color(0xFFBBDEFB),
    secondary = Color(0xFF80CBC4),
    onSecondary = Color(0xFF00695C),
    tertiary = Color(0xFFCE93D8),
    onTertiary = Color(0xFF4A0072),
    background = Color(0xFF121212),
    onBackground = Color(0xFFE0E0E0),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE0E0E0),
    surfaceVariant = Color(0xFF2C2C2C),
    onSurfaceVariant = Color(0xFFBDBDBD),
    error = Color(0xFFCF6679),
    onError = Color.Black
)

// ── BLUE ──────────────────────────────────────────────────────────────────
private val BlueLight = lightColorScheme(
    primary = Color(0xFF1565C0),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD1E4FF),
    onPrimaryContainer = Color(0xFF001D36),
    secondary = Color(0xFF535F70),
    onSecondary = Color.White,
    tertiary = Color(0xFF1976D2),
    onTertiary = Color.White,
    background = Color(0xFFF8FAFE),
    onBackground = Color(0xFF1A1C1E),
    surface = Color.White,
    onSurface = Color(0xFF1A1C1E),
    surfaceVariant = Color(0xFFE8EAEF),
    onSurfaceVariant = Color(0xFF44474F),
    error = Color(0xFFB00020),
    onError = Color.White
)

private val BlueDark = darkColorScheme(
    primary = Color(0xFF9ECAFF),
    onPrimary = Color(0xFF003258),
    primaryContainer = Color(0xFF00497D),
    onPrimaryContainer = Color(0xFFD1E4FF),
    secondary = Color(0xFFBBC7DB),
    onSecondary = Color(0xFF253140),
    tertiary = Color(0xFF90CAF9),
    onTertiary = Color(0xFF0D47A1),
    background = Color(0xFF1A1C1E),
    onBackground = Color(0xFFE2E2E6),
    surface = Color(0xFF1A1C1E),
    onSurface = Color(0xFFE2E2E6),
    surfaceVariant = Color(0xFF2A2C2F),
    onSurfaceVariant = Color(0xFFC4C6D0),
    error = Color(0xFFCF6679),
    onError = Color.Black
)

// ── GREEN ─────────────────────────────────────────────────────────────────
private val GreenLight = lightColorScheme(
    primary = Color(0xFF2E7D32),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFC8E6C9),
    onPrimaryContainer = Color(0xFF1B5E20),
    secondary = Color(0xFF558B2F),
    onSecondary = Color.White,
    tertiary = Color(0xFF388E3C),
    onTertiary = Color.White,
    background = Color(0xFFF1F8E9),
    onBackground = Color(0xFF1B2518),
    surface = Color.White,
    onSurface = Color(0xFF1B2518),
    surfaceVariant = Color(0xFFE0E8DA),
    onSurfaceVariant = Color(0xFF52634F),
    error = Color(0xFFB00020),
    onError = Color.White
)

private val GreenDark = darkColorScheme(
    primary = Color(0xFFA5D6A7),
    onPrimary = Color(0xFF1B5E20),
    primaryContainer = Color(0xFF2E7D32),
    onPrimaryContainer = Color(0xFFC8E6C9),
    secondary = Color(0xFFAED581),
    onSecondary = Color(0xFF33691E),
    tertiary = Color(0xFF81C784),
    onTertiary = Color(0xFF1B5E20),
    background = Color(0xFF1B2518),
    onBackground = Color(0xFFDDE5D8),
    surface = Color(0xFF1B2518),
    onSurface = Color(0xFFDDE5D8),
    surfaceVariant = Color(0xFF2B3527),
    onSurfaceVariant = Color(0xFF9DAD97),
    error = Color(0xFFCF6679),
    onError = Color.Black
)

// ── PURPLE ────────────────────────────────────────────────────────────────
private val PurpleLight = lightColorScheme(
    primary = Color(0xFF6A1B9A),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE1BEE7),
    onPrimaryContainer = Color(0xFF4A0072),
    secondary = Color(0xFF7B1FA2),
    onSecondary = Color.White,
    tertiary = Color(0xFF9C27B0),
    onTertiary = Color.White,
    background = Color(0xFFF3E5F5),
    onBackground = Color(0xFF1E1A1E),
    surface = Color.White,
    onSurface = Color(0xFF1E1A1E),
    surfaceVariant = Color(0xFFE9DAF0),
    onSurfaceVariant = Color(0xFF4D444D),
    error = Color(0xFFB00020),
    onError = Color.White
)

private val PurpleDark = darkColorScheme(
    primary = Color(0xFFCE93D8),
    onPrimary = Color(0xFF4A0072),
    primaryContainer = Color(0xFF6A1B9A),
    onPrimaryContainer = Color(0xFFE1BEE7),
    secondary = Color(0xFFBA68C8),
    onSecondary = Color(0xFF4A0072),
    tertiary = Color(0xFFAB47BC),
    onTertiary = Color(0xFF4A0072),
    background = Color(0xFF1E1A1E),
    onBackground = Color(0xFFEAE0EA),
    surface = Color(0xFF1E1A1E),
    onSurface = Color(0xFFEAE0EA),
    surfaceVariant = Color(0xFF2E2830),
    onSurfaceVariant = Color(0xFFCBC4CB),
    error = Color(0xFFCF6679),
    onError = Color.Black
)

// ── ORANGE ────────────────────────────────────────────────────────────────
private val OrangeLight = lightColorScheme(
    primary = Color(0xFFE65100),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFE0B2),
    onPrimaryContainer = Color(0xFFBF360C),
    secondary = Color(0xFFFF6F00),
    onSecondary = Color.White,
    tertiary = Color(0xFFFF9800),
    onTertiary = Color.White,
    background = Color(0xFFFFF8F0),
    onBackground = Color(0xFF201A14),
    surface = Color.White,
    onSurface = Color(0xFF201A14),
    surfaceVariant = Color(0xFFFFF0E0),
    onSurfaceVariant = Color(0xFF4F4539),
    error = Color(0xFFB00020),
    onError = Color.White
)

private val OrangeDark = darkColorScheme(
    primary = Color(0xFFFFB74D),
    onPrimary = Color(0xFFBF360C),
    primaryContainer = Color(0xFFE65100),
    onPrimaryContainer = Color(0xFFFFE0B2),
    secondary = Color(0xFFFFCA28),
    onSecondary = Color(0xFFFF6F00),
    tertiary = Color(0xFFFFA726),
    onTertiary = Color(0xFFBF360C),
    background = Color(0xFF201A14),
    onBackground = Color(0xFFEDE0D4),
    surface = Color(0xFF201A14),
    onSurface = Color(0xFFEDE0D4),
    surfaceVariant = Color(0xFF302A24),
    onSurfaceVariant = Color(0xFFD0C5BA),
    error = Color(0xFFCF6679),
    onError = Color.Black
)

// ── RED ───────────────────────────────────────────────────────────────────
private val RedLight = lightColorScheme(
    primary = Color(0xFFC62828),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFCDD2),
    onPrimaryContainer = Color(0xFF7F0000),
    secondary = Color(0xFFD32F2F),
    onSecondary = Color.White,
    tertiary = Color(0xFFEF5350),
    onTertiary = Color.White,
    background = Color(0xFFFFF5F5),
    onBackground = Color(0xFF201A19),
    surface = Color.White,
    onSurface = Color(0xFF201A19),
    surfaceVariant = Color(0xFFFFE8E8),
    onSurfaceVariant = Color(0xFF534341),
    error = Color(0xFFB00020),
    onError = Color.White
)

private val RedDark = darkColorScheme(
    primary = Color(0xFFEF9A9A),
    onPrimary = Color(0xFF7F0000),
    primaryContainer = Color(0xFFC62828),
    onPrimaryContainer = Color(0xFFFFCDD2),
    secondary = Color(0xFFE57373),
    onSecondary = Color(0xFF7F0000),
    tertiary = Color(0xFFEF9A9A),
    onTertiary = Color(0xFF7F0000),
    background = Color(0xFF201A19),
    onBackground = Color(0xFFEDE0DE),
    surface = Color(0xFF201A19),
    onSurface = Color(0xFFEDE0DE),
    surfaceVariant = Color(0xFF302A29),
    onSurfaceVariant = Color(0xFFD8C2BF),
    error = Color(0xFFCF6679),
    onError = Color.Black
)

// ── GLASS THEME HELPER ──────────────────────────────────────────────────────
@Composable
fun isDarkTheme(): Boolean = MaterialTheme.colorScheme.background.luminance() < 0.5

fun getColorScheme(scheme: ColorSchemes, dark: Boolean): ColorScheme = when (scheme) {
    ColorSchemes.DEFAULT -> if (dark) DefaultDark else DefaultLight
    ColorSchemes.BLUE -> if (dark) BlueDark else BlueLight
    ColorSchemes.GREEN -> if (dark) GreenDark else GreenLight
    ColorSchemes.PURPLE -> if (dark) PurpleDark else PurpleLight
    ColorSchemes.ORANGE -> if (dark) OrangeDark else OrangeLight
    ColorSchemes.RED -> if (dark) RedDark else RedLight
}

@Composable
fun VidoPlayTheme(
    themePreferences: ThemePreferences,
    dynamicColorScheme: ColorScheme? = null,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val systemDark = isSystemInDarkTheme()

    val isDark = when (themePreferences.themeMode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.SYSTEM -> systemDark
    }

    // Determine which color scheme to use
    val colorScheme = when {
        // Priority 1: Dynamic Color (Material You)
        themePreferences.useDynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            dynamicColorScheme ?: if (isDark) {
                dynamicDarkColorScheme(context)
            } else {
                dynamicLightColorScheme(context)
            }
        }
        else -> getColorScheme(themePreferences.colorScheme, isDark)
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as androidx.activity.ComponentActivity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDark
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = VideoPlayerTypography,
        shapes = VideoPlayerShapes,
        content = content
    )
}