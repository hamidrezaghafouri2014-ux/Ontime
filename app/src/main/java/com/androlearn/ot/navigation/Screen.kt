package com.androlearn.ot.navigation

sealed class Screen(val route: String) {
    object HomeScreen : Screen("home")
    object HelpScreen : Screen("help")
    object DCScreen : Screen("dc")
    object AddTask : Screen("add_task")
    object Settings : Screen("settings")
    object TimerScreen : Screen("timer/{taskId}") {
        fun createRoute(taskId: Int) = "timer/$taskId"
    }
    object FocusTimer : Screen("focus_timer")

    data object SmartRecovery : Screen("smart_recovery/{date}/{day}") {
        fun createRoute(date: String, day: String): String {
            return "smart_recovery/$date/$day"
        }
    }

    object Upgrade : Screen("upgrade")
}