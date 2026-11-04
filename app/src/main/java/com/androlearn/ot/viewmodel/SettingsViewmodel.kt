package com.androlearn.ot.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androlearn.ot.data.preferences.ThemePreferences
import com.androlearn.ot.ui.theme.AppTheme
import com.androlearn.ot.ui.theme.toComposeColor
import com.androlearn.ot.ui.theme.toHex
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val themePreferences: ThemePreferences
) : ViewModel() {

    val isDarkMode: StateFlow<Boolean> =
        themePreferences.darkMode
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                true
            )

    val appTheme: StateFlow<AppTheme> =
        themePreferences.appTheme
            .map { value ->
                try {
                    AppTheme.valueOf(value)
                } catch (e: IllegalArgumentException) {
                    AppTheme.EMERALD
                }
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                AppTheme.EMERALD
            )

    fun toggleDarkMode() {
        viewModelScope.launch {
            themePreferences.setDarkMode(
                !isDarkMode.value
            )
        }
    }

    fun setTheme(theme: AppTheme) {
        viewModelScope.launch {
            themePreferences.setAppTheme(
                theme.name
            )
        }
    }

    val customPrimary: StateFlow<Color> =
        themePreferences.customPrimary
            .map { it.toComposeColor() }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                Color(0xFF00C896)
            )

    val customSecondary: StateFlow<Color> =
        themePreferences.customSecondary
            .map { it.toComposeColor() }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                Color(0xFF5EEAD4)
            )

    fun setCustomPrimary(color: Color) {
        viewModelScope.launch {
            themePreferences.setCustomPrimary(
                color.toHex()
            )
        }
    }

    fun setCustomSecondary(color: Color) {
        viewModelScope.launch {
            themePreferences.setCustomSecondary(
                color.toHex()
            )
        }
    }

    fun setCustomPrimaryAndActivate(color: Color) {
        viewModelScope.launch {

            themePreferences.setCustomPrimary(
                color.toHex()
            )

            themePreferences.setAppTheme(
                AppTheme.CUSTOM.name
            )
        }
    }

}