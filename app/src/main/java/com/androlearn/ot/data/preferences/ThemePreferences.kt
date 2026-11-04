package com.androlearn.ot.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ThemePreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    private val DARK_MODE =
        booleanPreferencesKey("dark_mode")

    private val APP_THEME =
        stringPreferencesKey("app_theme")

    private val CUSTOM_PRIMARY =
        stringPreferencesKey("custom_primary")

    private val CUSTOM_SECONDARY =
        stringPreferencesKey("custom_secondary")


    // -------------------------
    // Dark Mode
    // -------------------------

    val darkMode: Flow<Boolean> =
        dataStore.data.map { preferences ->
            preferences[DARK_MODE] ?: true
        }


    suspend fun setDarkMode(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[DARK_MODE] = enabled
        }
    }


    // -------------------------
    // App Theme
    // -------------------------

    val appTheme: Flow<String> =
        dataStore.data.map { preferences ->
            preferences[APP_THEME] ?: "EMERALD"
        }


    suspend fun setAppTheme(theme: String) {
        dataStore.edit { preferences ->
            preferences[APP_THEME] = theme
        }
    }


    // -------------------------
    // Custom Colors
    // -------------------------

    val customPrimary: Flow<String> =
        dataStore.data.map { preferences ->
            preferences[CUSTOM_PRIMARY] ?: "#00C896"
        }


    val customSecondary: Flow<String> =
        dataStore.data.map { preferences ->
            preferences[CUSTOM_SECONDARY] ?: "#5EEAD4"
        }


    suspend fun setCustomPrimary(color: String) {
        dataStore.edit { preferences ->
            preferences[CUSTOM_PRIMARY] = color
        }
    }


    suspend fun setCustomSecondary(color: String) {
        dataStore.edit { preferences ->
            preferences[CUSTOM_SECONDARY] = color
        }
    }
}