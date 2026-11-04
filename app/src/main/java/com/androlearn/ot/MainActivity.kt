package com.androlearn.ot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.androlearn.ot.navigation.NavGraph
import com.androlearn.ot.ui.theme.OnTimeTheme
import com.androlearn.ot.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        setContent {
            val isDarkMode by settingsViewModel.isDarkMode
                .collectAsStateWithLifecycle()

            val selectedTheme by settingsViewModel.appTheme
                .collectAsStateWithLifecycle()

            val customPrimary by settingsViewModel.customPrimary
                .collectAsStateWithLifecycle()

            val customSecondary by settingsViewModel.customSecondary
                .collectAsStateWithLifecycle()

            OnTimeTheme(
                darkTheme = isDarkMode,
                appTheme = selectedTheme,
                customPrimary = customPrimary,
                customSecondary = customSecondary
            ) {
                val navController = rememberNavController()

                Scaffold { innerPadding ->
                    NavGraph(
                        navController = navController,
                        paddingValues = innerPadding,
                        isDarkMode = isDarkMode,
                        onThemeToggle = {
                            settingsViewModel.toggleDarkMode()
                        },
                        selectedTheme = selectedTheme,
                        onThemeSelected = {
                            settingsViewModel.setTheme(it)
                        },
                        customPrimary = customPrimary,
                        onCustomPrimaryChange = {
                            settingsViewModel.setCustomPrimaryAndActivate(it)
                        }
                    )
                }
            }
        }
    }
}