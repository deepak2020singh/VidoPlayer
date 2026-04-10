package com.foss.vidoplay.data.local.pref

import com.foss.vidoplay.presentation.common.ViewMode


enum class ThemeMode { SYSTEM, LIGHT, DARK }
enum class ColorSchemes { DEFAULT, BLUE, GREEN, PURPLE, ORANGE, RED }

data class ThemePreferences(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val colorScheme: ColorSchemes = ColorSchemes.DEFAULT,
    val useDynamicColor: Boolean = false,
    val viewMode: ViewMode = ViewMode.LIST
)