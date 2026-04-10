package com.foss.vidoplay.data.repos


import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.foss.vidoplay.data.local.pref.ColorSchemes
import com.foss.vidoplay.data.local.pref.ThemeMode
import com.foss.vidoplay.data.local.pref.ThemePreferences
import com.foss.vidoplay.presentation.common.ViewMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ThemePreferencesRepository(
    private val dataStore: DataStore<Preferences>
)
{
    companion object {
        val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
        val COLOR_SCHEME_KEY = stringPreferencesKey("color_scheme")
        val DYNAMIC_COLOR_KEY = booleanPreferencesKey("dynamic_color")
        val VIEW_MODE_KEY = stringPreferencesKey("view_mode")
    }

    val themePreferences: Flow<ThemePreferences> = dataStore.data.map { prefs ->
        ThemePreferences(
            themeMode = ThemeMode.valueOf(prefs[THEME_MODE_KEY] ?: ThemeMode.SYSTEM.name),
            colorScheme = ColorSchemes.valueOf(
                prefs[COLOR_SCHEME_KEY] ?: ColorSchemes.DEFAULT.name
            ),
            useDynamicColor = prefs[DYNAMIC_COLOR_KEY] ?: false,
            viewMode = try {
                ViewMode.valueOf(prefs[VIEW_MODE_KEY] ?: ViewMode.LIST.name)
            } catch (e: Exception) {
                ViewMode.LIST
            }
        )
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { it[THEME_MODE_KEY] = mode.name }
    }

    suspend fun setColorScheme(scheme: ColorSchemes) {
        dataStore.edit { it[COLOR_SCHEME_KEY] = scheme.name }
    }

    suspend fun setDynamicColor(enabled: Boolean) {
        dataStore.edit { it[DYNAMIC_COLOR_KEY] = enabled }
    }

    suspend fun setViewMode(viewMode: ViewMode) {
        dataStore.edit { it[VIEW_MODE_KEY] = viewMode.name }
    }
}