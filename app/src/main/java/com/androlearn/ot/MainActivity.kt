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
import android.app.AlarmManager
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.androlearn.ot.data.repository.TaskRepository
import com.androlearn.ot.util.AlarmScheduler
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val settingsViewModel: SettingsViewModel by viewModels()
    @Inject
    lateinit var taskRepository: TaskRepository


    private val exactAlarmPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            val alarmManager =
                getSystemService(ALARM_SERVICE) as AlarmManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val isGranted = alarmManager.canScheduleExactAlarms()

                if (isGranted) {
                    getSharedPreferences(
                        "ontime_permissions",
                        MODE_PRIVATE
                    ).edit()
                        .putBoolean("exact_alarm_requested", false)
                        .apply()

                    rescheduleAllTasks()
                    requestNotificationPermission()

                } else {
                    getSharedPreferences(
                        "ontime_permissions",
                        MODE_PRIVATE
                    ).edit()
                        .putBoolean("exact_alarm_requested", false)
                        .apply()
                }
            }
        }

    private val notificationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                // مجوز اعلان فعال شد
            }
        }

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
                        },
                        onRequestExactAlarmPermission = {
                            requestExactAlarmPermission(force = true)
                        }
                    )
                }
            }
        }
    }

    private fun requestExactAlarmPermission(
        force: Boolean = false
    ) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return

        val alarmManager =
            getSystemService(ALARM_SERVICE) as AlarmManager

        if (alarmManager.canScheduleExactAlarms()) {
            requestNotificationPermission()
            return
        }

        val preferences = getSharedPreferences(
            "ontime_permissions",
            MODE_PRIVATE
        )

        val alreadyRequested = preferences.getBoolean(
            "exact_alarm_requested",
            false
        )

        if (alreadyRequested && !force) return

        preferences.edit()
            .putBoolean("exact_alarm_requested", true)
            .apply()

        val intent = Intent(
            Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
            Uri.parse("package:$packageName")
        )

        exactAlarmPermissionLauncher.launch(intent)
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            val permissionGranted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!permissionGranted) {
                notificationPermissionLauncher.launch(
                    Manifest.permission.POST_NOTIFICATIONS
                )
            }
        }
    }

    private fun rescheduleAllTasks() {
        lifecycleScope.launch {
            val tasks = taskRepository.getAllTasks().first()

            val alarmScheduler = AlarmScheduler(this@MainActivity)

            tasks
                .filter { it.notificationEnabled }
                .forEach { task ->
                    val scheduled = alarmScheduler.scheduleTask(task)

                    if (!scheduled) {
                        android.util.Log.w(
                            "OnTime",
                            "Could not schedule alarm for task: ${task.id}"
                        )
                    }
                }
        }
    }
}