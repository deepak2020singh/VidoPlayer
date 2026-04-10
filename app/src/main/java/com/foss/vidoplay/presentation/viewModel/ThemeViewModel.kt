package com.foss.vidoplay.presentation.viewModel

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foss.vidoplay.data.local.pref.ColorSchemes
import com.foss.vidoplay.data.local.pref.ThemeMode
import com.foss.vidoplay.data.local.pref.ThemePreferences
import com.foss.vidoplay.data.repos.ThemePreferencesRepository
import com.foss.vidoplay.presentation.common.ViewMode
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ThemeViewModel(
    private val repo: ThemePreferencesRepository
) : ViewModel()
{
    val themePreferences: StateFlow<ThemePreferences> = repo.themePreferences.stateIn(
        scope = viewModelScope, started = SharingStarted.Eagerly, initialValue = ThemePreferences()
    )

    @Composable
    fun isDarkThemeActive(): Boolean {
        val preferences = themePreferences.collectAsState().value
        return when (preferences.themeMode) {
            ThemeMode.DARK -> true
            ThemeMode.LIGHT -> false
            ThemeMode.SYSTEM -> isSystemInDarkTheme()
        }
    }

    @Composable
    fun getCurrentColorScheme() = when {
        themePreferences.collectAsState().value.useDynamicColor && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (isDarkThemeActive()) {
                androidx.compose.material3.dynamicDarkColorScheme(context)
            } else {
                androidx.compose.material3.dynamicLightColorScheme(context)
            }
        }
        else -> {
            com.foss.vidoplay.ui.theme.getColorScheme(
                themePreferences.collectAsState().value.colorScheme, isDarkThemeActive()
            )
        }
    }

    fun getFolderViewMode(): ViewMode = themePreferences.value.viewMode
    fun getVideoViewMode(): ViewMode = themePreferences.value.viewMode

    fun setFolderViewMode(viewMode: ViewMode) {
        viewModelScope.launch { repo.setViewMode(viewMode) }
    }

    fun setVideoViewMode(viewMode: ViewMode) {
        viewModelScope.launch { repo.setViewMode(viewMode) }
    }

    fun setViewMode(viewMode: ViewMode) {
        viewModelScope.launch { repo.setViewMode(viewMode) }
    }

    fun toggleFolderViewMode() {
        val newMode = if (getFolderViewMode() == ViewMode.LIST) {
            ViewMode.GRID
        } else {
            ViewMode.LIST
        }
        setFolderViewMode(newMode)
    }

    fun toggleVideoViewMode() {
        val newMode = if (getVideoViewMode() == ViewMode.LIST) {
            ViewMode.GRID
        } else {
            ViewMode.LIST
        }
        setVideoViewMode(newMode)
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { repo.setThemeMode(mode) }
    }

    fun setColorScheme(scheme: ColorSchemes) {
        viewModelScope.launch { repo.setColorScheme(scheme) }
    }

    fun setDynamicColor(enabled: Boolean) {
        viewModelScope.launch { repo.setDynamicColor(enabled) }
    }
}