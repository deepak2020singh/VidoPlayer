package com.foss.vidoplay.presentation.ui

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.outlined.BrightnessMedium
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.foss.vidoplay.data.local.pref.ColorSchemes
import com.foss.vidoplay.data.local.pref.ThemeMode
import com.foss.vidoplay.presentation.common.GlassTokens
import com.foss.vidoplay.presentation.common.glassCard
import com.foss.vidoplay.presentation.common.glassChip
import com.foss.vidoplay.presentation.viewModel.ThemeViewModel
import com.foss.vidoplay.ui.theme.getColorScheme
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    innerPadding: PaddingValues,
    themeViewModel: ThemeViewModel = koinViewModel(),
    onNavigateToAbout: () -> Unit = {}
) {
    val textPrimary = GlassTokens.getTextPrimary()
    val textTertiary = GlassTokens.getTextTertiary()
    val chipBorder = GlassTokens.getChipBorder()
    val isDark = GlassTokens.isDarkTheme()

    val themePrefs by themeViewModel.themePreferences.collectAsState()
    val isDarkTheme = themeViewModel.isDarkThemeActive()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isDark) Color(0xFF0A0A0A) else Color(0xFFF5F5F5))
    ) {
        TopAppBar(
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Glass icon background
                    Surface(
                        modifier = Modifier
                            .size(40.dp)
                            .glassChip(cornerRadius = 12.dp),
                        color = Color.Transparent
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.ColorLens,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Text(
                        "Settings",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                GlassSettingsSection(title = "Theme", icon = Icons.Outlined.DarkMode) {
                    GlassThemeModeSelector(
                        current = themePrefs.themeMode,
                        onSelect = { themeViewModel.setThemeMode(it) }
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                        color = chipBorder
                    )

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        GlassSettingsSwitchItem(
                            icon = Icons.Outlined.Palette,
                            title = "Material You",
                            description = "Use your wallpaper colors",
                            checked = themePrefs.useDynamicColor,
                            onCheckedChange = { themeViewModel.setDynamicColor(it) }
                        )
                    }
                }
            }

            if (!themePrefs.useDynamicColor) {
                item {
                    GlassSettingsSection(title = "Color Scheme", icon = Icons.Outlined.Palette) {
                        GlassColorSchemePicker(
                            current = themePrefs.colorScheme,
                            isDark = isDarkTheme,
                            onSelect = { scheme -> themeViewModel.setColorScheme(scheme) }
                        )
                    }
                }
            }

            item {
                GlassSettingsSection(title = "About", icon = Icons.Outlined.Info) {
                    GlassAboutItem(
                        icon = Icons.Outlined.Info,
                        title = "Version",
                        description = "1.0.0 (Build 101)"
                    )
                    GlassSettingsItem(
                        icon = Icons.Outlined.Star,
                        title = "Rate the App",
                        description = "Love VidoPlay? Rate us on Play Store",
                        onClick = {}
                    )
                    GlassSettingsItem(
                        icon = Icons.Outlined.Info,
                        title = "Terms & Privacy",
                        description = "Read our terms and privacy policy",
                        onClick = onNavigateToAbout
                    )
                }
            }

            item {
                Text(
                    "VidoPlay v1.0.0",
                    style = MaterialTheme.typography.bodySmall,
                    color = textTertiary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.padding(innerPadding))
            }
        }
    }
}

// ==================== GLASS THEME MODE SELECTOR ====================

@Composable
fun GlassThemeModeSelector(
    current: ThemeMode,
    onSelect: (ThemeMode) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        GlassThemeModeChip(
            label = "System",
            icon = Icons.Outlined.BrightnessMedium,
            selected = current == ThemeMode.SYSTEM,
            modifier = Modifier.weight(1f),
            onClick = { onSelect(ThemeMode.SYSTEM) }
        )
        GlassThemeModeChip(
            label = "Light",
            icon = Icons.Outlined.LightMode,
            selected = current == ThemeMode.LIGHT,
            modifier = Modifier.weight(1f),
            onClick = { onSelect(ThemeMode.LIGHT) }
        )
        GlassThemeModeChip(
            label = "Dark",
            icon = Icons.Outlined.DarkMode,
            selected = current == ThemeMode.DARK,
            modifier = Modifier.weight(1f),
            onClick = { onSelect(ThemeMode.DARK) }
        )
    }
}

@Composable
fun GlassThemeModeChip(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val textSecondary = GlassTokens.getTextSecondary()

    Surface(
        modifier = modifier
            .glassChip(cornerRadius = 12.dp)
            .clickable { onClick() },
        color = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = if (selected) Color.White else textSecondary,
                modifier = Modifier.size(20.dp)
            )
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = if (selected) Color.White else textSecondary,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

// ==================== GLASS COLOR SCHEME PICKER ====================

@Composable
fun GlassColorSchemePicker(
    current: ColorSchemes,
    isDark: Boolean,
    onSelect: (ColorSchemes) -> Unit
) {
    val textTertiary = GlassTokens.getTextTertiary()

    val schemes = listOf(
        ColorSchemes.DEFAULT to "Default",
        ColorSchemes.BLUE to "Blue",
        ColorSchemes.GREEN to "Green",
        ColorSchemes.PURPLE to "Purple",
        ColorSchemes.ORANGE to "Orange",
        ColorSchemes.RED to "Red",
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            schemes.forEach { (scheme, label) ->
                val color = getColorScheme(scheme, isDark).primary
                GlassColorSwatch(
                    color = color,
                    label = label,
                    selected = current == scheme,
                    onClick = { onSelect(scheme) }
                )
            }
        }

        GlassThemePreviewStrip(scheme = current, isDark = isDark)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Text(
                text = "💡 Tip: Enable Material You in Theme settings for dynamic colors from your wallpaper",
                style = MaterialTheme.typography.labelSmall,
                color = textTertiary,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun GlassThemePreviewStrip(scheme: ColorSchemes, isDark: Boolean) {
    val colors = getColorScheme(scheme, isDark)
    val textSecondary = GlassTokens.getTextSecondary()


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .glassCard(cornerRadius = 12.dp)
            .padding(12.dp)
    ) {
        Text(
            "Theme Preview",
            style = MaterialTheme.typography.labelMedium,
            color = textSecondary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(12.dp)),
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(colors.primary)
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(colors.secondary)
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(colors.tertiary)
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(colors.primaryContainer)
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(colors.surface)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "Sample Text",
            style = MaterialTheme.typography.bodyMedium,
            color = colors.onSurface
        )
        Text(
            "Secondary Text",
            style = MaterialTheme.typography.bodySmall,
            color = colors.onSurfaceVariant
        )
    }
}

@Composable
fun GlassColorSwatch(
    color: Color,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val textSecondary = GlassTokens.getTextSecondary()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(color)
                .then(
                    if (selected) Modifier.border(
                        3.dp, Color.White, CircleShape
                    ) else Modifier.border(1.dp, Color.Transparent, CircleShape)
                )
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            if (selected) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = textSecondary
        )
    }
}

// ==================== GLASS SETTINGS SECTION ====================

@Composable
fun GlassSettingsSection(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .glassCard(cornerRadius = 16.dp)
            ) {
                content()
            }
        }
    }
}

// ==================== GLASS SETTINGS ITEM ====================

@Composable
fun GlassSettingsItem(
    icon: ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit,
    showBadge: Boolean = false,
    badgeText: String = "",
    trailingIcon: ImageVector = Icons.AutoMirrored.Filled.ArrowForward
) {
    val textPrimary = GlassTokens.getTextPrimary()
    val textSecondary = GlassTokens.getTextSecondary()
    val chipBorder = GlassTokens.getChipBorder()

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = textPrimary
                    )

                    if (showBadge) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = badgeText,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Icon(
                trailingIcon,
                contentDescription = null,
                tint = textSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
    }

    HorizontalDivider(
        modifier = Modifier.padding(start = 72.dp, end = 16.dp),
        thickness = DividerDefaults.Thickness,
        color = chipBorder
    )
}

// ==================== GLASS SWITCH ITEM ====================

@Composable
fun GlassSettingsSwitchItem(
    icon: ImageVector,
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val textPrimary = GlassTokens.getTextPrimary()
    val textSecondary = GlassTokens.getTextSecondary()
    val chipBorder = GlassTokens.getChipBorder()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = textPrimary
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                uncheckedThumbColor = textSecondary,
                uncheckedTrackColor = Color.White.copy(alpha = 0.15f)
            )
        )
    }

    HorizontalDivider(
        modifier = Modifier.padding(start = 72.dp, end = 16.dp),
        thickness = DividerDefaults.Thickness,
        color = chipBorder
    )
}

// ==================== GLASS ABOUT ITEM ====================

@Composable
fun GlassAboutItem(
    icon: ImageVector,
    title: String,
    description: String
) {
    val textPrimary = GlassTokens.getTextPrimary()
    val textSecondary = GlassTokens.getTextSecondary()
    val chipBorder = GlassTokens.getChipBorder()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = textPrimary
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = textSecondary
            )
        }
    }

    HorizontalDivider(
        modifier = Modifier.padding(start = 72.dp, end = 16.dp),
        thickness = DividerDefaults.Thickness,
        color = chipBorder
    )
}