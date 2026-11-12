package com.androlearn.ot.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.androlearn.ot.data.local.database.AppDatabase
import com.androlearn.ot.util.AlarmScheduler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var database: AppDatabase

    override fun onReceive(context: Context, intent: Intent) {

        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val tasks = database.taskDao()
                    .getAllTasks()
                    .first()

                val scheduler = AlarmScheduler(context)

                tasks.forEach { task ->
                    if (task.notificationEnabled) {
                        runCatching {
                            scheduler.scheduleTask(task)
                        }
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e(
                    "BootReceiver",
                    "Failed to restore alarms",
                    e
                )
            } finally {
                pendingResult.finish()
            }
        }
    }
}