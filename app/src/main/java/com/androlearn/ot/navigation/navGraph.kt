package com.androlearn.ot.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.androlearn.ot.ui.recovery.SmartRecoveryScreen
import com.androlearn.ot.ui.screen.AddTask
import com.androlearn.ot.ui.screen.Help
import com.androlearn.ot.ui.screen.HomeScreen
import com.androlearn.ot.ui.screen.Settings
import com.androlearn.ot.ui.screen.TimerScreen
import com.androlearn.ot.ui.screen.DailyComponent
import com.androlearn.ot.ui.screen.FocusTimerScreen
import com.androlearn.ot.ui.screen.UpgradeScreen
import com.androlearn.ot.ui.theme.AppTheme
import com.androlearn.ot.util.RecoveryTime

@Composable
fun NavGraph(
    navController: NavHostController,
    paddingValues: PaddingValues,
    isDarkMode: Boolean,
    onThemeToggle: () -> Unit,
    selectedTheme: AppTheme,
    onThemeSelected: (AppTheme) -> Unit,
    customPrimary: Color,
    onCustomPrimaryChange: (Color) -> Unit,
    onRequestExactAlarmPermission: () -> Unit
) {

    NavHost(
        navController = navController,
        startDestination = Screen.HomeScreen.route
    ) {

        composable(Screen.HomeScreen.route) {
            HomeScreen(
                navController = navController,
                paddingValues = paddingValues,
                isDarkMode = isDarkMode,
                onThemeToggle = onThemeToggle
            )
        }

        composable(Screen.DCScreen.route) {
            DailyComponent(
                navController = navController,
                paddingValues = paddingValues
            )
        }

        composable(Screen.HelpScreen.route) {
            Help(
                navController = navController,
                paddingValues = paddingValues
            )
        }

        composable(Screen.AddTask.route) {
            AddTask(
                navController = navController,
                paddingValues = paddingValues,
                onRequestExactAlarmPermission = onRequestExactAlarmPermission
            )
        }

        composable(
            route = Screen.TimerScreen.route,
            arguments = listOf(
                navArgument("taskId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->

            val taskId =
                backStackEntry.arguments?.getInt("taskId") ?: -1

            TimerScreen(
                taskId = taskId,
                navController = navController
            )
        }

        composable(Screen.Settings.route) {
            Settings(
                navController = navController,
                paddingValues = paddingValues,
                isDarkTheme = isDarkMode,
                onThemeChange = onThemeToggle,
                selectedTheme = selectedTheme,
                onThemeSelected = onThemeSelected,
                customPrimary = customPrimary,
                onCustomPrimaryChange = onCustomPrimaryChange
            )
        }

        composable(Screen.FocusTimer.route) {
            FocusTimerScreen(navController = navController)
        }

        composable(
            route = Screen.SmartRecovery.route,
            arguments = listOf(
                navArgument("date") {
                    type = NavType.StringType
                },
                navArgument("day") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->

            val date = backStackEntry.arguments?.getString("date") ?: ""
            val day = backStackEntry.arguments?.getString("day") ?: ""

            SmartRecoveryScreen(
                date = date,
                day = day,
                nowMinute = RecoveryTime.currentMinute(),
                nowMillis = RecoveryTime.currentMillis()
            )
        }

        composable(Screen.Upgrade.route) {
            UpgradeScreen(
                navController = navController
            )
        }
    }
}