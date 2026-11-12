package com.androlearn.ot.receiver

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.androlearn.ot.MainActivity
import com.androlearn.ot.data.local.entity.TaskEntity
import com.androlearn.ot.util.AlarmScheduler
import com.androlearn.ot.worker.FollowUpWorker
import com.androlearn.ot.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import android.Manifest
import androidx.work.ExistingWorkPolicy

class NotificationReceiver : BroadcastReceiver() {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(
        context: Context,
        intent: Intent
    ) {

        val endHour = intent.getIntExtra("TASK_END_HOUR", 0)
        val endMinute = intent.getIntExtra("TASK_END_MINUTE", 0)

        val taskId =
            intent.getIntExtra("TASK_ID", -1)

        if (taskId == -1) return

        val notificationEnabled = intent.getBooleanExtra(
            "TASK_NOTIFICATION_ENABLED",
            true
        )

        if (!notificationEnabled) return

        val taskTitle =
            intent.getStringExtra("TASK_TITLE")
                ?: "OnTime"

        val question =
            intent.getStringExtra("TASK_QUESTION")
                ?: "کارتو انجام دادی؟"

        val startMillis =
            intent.getLongExtra(
                "TASK_START_MILLIS",
                System.currentTimeMillis()
            )

        showNotification(
            context = context,
            taskId = taskId,
            taskTitle = taskTitle,
            question = question
        )

        showNotification(
            context = context,
            taskId = taskId,
            taskTitle = taskTitle,
            question = question
        )

        scheduleNextOccurrence(
            context = context,
            intent = intent
        )

        scheduleFollowUp(
            context = context,
            taskId = taskId,
            startMillis = startMillis,
            endHour = endHour,
            endMinute = endMinute
        )
    }

    private fun showNotification(
        context: Context,
        taskId: Int,
        taskTitle: String,
        question: String
    ) {

        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val channelId = "tasks"

        val notificationManager =
            context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                channelId,
                "Task Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description =
                    "Notifications for OnTime tasks"
            }

            notificationManager.createNotificationChannel(
                channel
            )
        }

        val openIntent =
            Intent(
                context,
                MainActivity::class.java
            ).apply {

                putExtra(
                    "TARGET_SCREEN",
                    "timer"
                )

                putExtra(
                    "TASK_ID",
                    taskId
                )

                flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

        val pendingIntent =
            PendingIntent.getActivity(
                context,
                taskId,
                openIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

        val notification =
            NotificationCompat.Builder(
                context,
                channelId
            )
                .setSmallIcon(
                    R.drawable.ic_launcher_foreground
                )
                .setContentTitle(taskTitle)
                .setContentText(question)
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(question)
                )
                .setPriority(
                    NotificationCompat.PRIORITY_HIGH
                )
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build()

        notificationManager.notify(
            taskId,
            notification
        )
    }

    private fun scheduleFollowUp(
        context: Context,
        taskId: Int,
        startMillis: Long,
        endHour: Int,
        endMinute: Int
    ) {

        val startCalendar = Calendar.getInstance().apply {
            timeInMillis = startMillis
        }

        val endCalendar = startCalendar.clone() as Calendar

        endCalendar.set(Calendar.HOUR_OF_DAY, endHour)
        endCalendar.set(Calendar.MINUTE, endMinute)
        endCalendar.set(Calendar.SECOND, 0)
        endCalendar.set(Calendar.MILLISECOND, 0)

        if (endCalendar.timeInMillis <= startMillis) {
            endCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        val followUpTime =
            endCalendar.timeInMillis +
                    TimeUnit.MINUTES.toMillis(20)

        val delay = (
                followUpTime - System.currentTimeMillis()
                ).coerceAtLeast(1_000L)

        val data = Data.Builder()
            .putInt("TASK_ID", taskId)
            .putString(
                "TASK_DATE",
                SimpleDateFormat("yyyy-MM-dd", Locale.US)
                    .format(Date(startMillis))
            )
            .build()

        val workRequest =
            OneTimeWorkRequestBuilder<FollowUpWorker>()
                .setInputData(data)
                .addTag("follow_up_$taskId")
                .setInitialDelay(
                    delay,
                    TimeUnit.MILLISECONDS
                )
                .build()

        WorkManager
            .getInstance(context)
            .enqueueUniqueWork(
                "follow_up_$taskId",
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
    }

    private fun scheduleNextOccurrence(
        context: Context,
        intent: Intent
    ) {

        val taskId =
            intent.getIntExtra(
                "TASK_ID",
                -1
            )

        if (taskId == -1) return

        val title =
            intent.getStringExtra(
                "TASK_TITLE"
            ) ?: "OnTime"

        val question =
            intent.getStringExtra(
                "TASK_QUESTION"
            ) ?: "کارتو انجام دادی؟"

        val selectedDays =
            intent.getStringExtra(
                "TASK_SELECTED_DAYS"
            ) ?: return

        val startHour =
            intent.getIntExtra(
                "TASK_START_HOUR",
                0
            )

        val startMinute =
            intent.getIntExtra(
                "TASK_START_MINUTE",
                0
            )

        val endHour =
            intent.getIntExtra(
                "TASK_END_HOUR",
                0
            )

        val endMinute =
            intent.getIntExtra(
                "TASK_END_MINUTE",
                0
            )

        val reminderMinutes =
            intent.getIntExtra(
                "TASK_REMINDER_MINUTES",
                0
            )

        val notificationEnabled =
            intent.getBooleanExtra(
                "TASK_NOTIFICATION_ENABLED",
                true
            )

        val endDateMillis =
            intent.getLongExtra(
                "TASK_END_DATE_MILLIS",
                0L
            )

        val endDateText =
            intent.getStringExtra(
                "TASK_END_DATE_TEXT"
            ) ?: ""

        if (endDateMillis > 0L && System.currentTimeMillis() > endDateMillis) {
            return
        }

        val task =
            TaskEntity(
                id = taskId,
                title = title,
                startHour = startHour,
                startMinute = startMinute,
                endHour = endHour,
                endMinute = endMinute,
                color = intent.getIntExtra("TASK_COLOR", 0),
                priority = intent.getIntExtra("TASK_PRIORITY", 2),
                deadlineMillis = intent.getLongExtra("TASK_DEADLINE_MILLIS", -1L)
                    .takeIf { it >= 0L },
                isMovable = intent.getBooleanExtra("TASK_IS_MOVABLE", true),
                selectedDays = selectedDays,
                endDateMillis = endDateMillis,
                endDateText = endDateText,
                notificationEnabled = notificationEnabled,
                notificationQuestion = question,
                reminderMinutes = reminderMinutes
            )

        AlarmScheduler(context)
            .scheduleTask(task)
    }
}