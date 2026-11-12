package com.androlearn.ot.util

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.work.WorkManager
import com.androlearn.ot.data.local.entity.TaskEntity
import com.androlearn.ot.receiver.NotificationReceiver
import java.util.Calendar

class AlarmScheduler(
    private val context: Context
) {

    private val alarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    private val dayToCalendar = mapOf(
        "Sun" to Calendar.SUNDAY,
        "Mon" to Calendar.MONDAY,
        "Tue" to Calendar.TUESDAY,
        "Wed" to Calendar.WEDNESDAY,
        "Thu" to Calendar.THURSDAY,
        "Fri" to Calendar.FRIDAY,
        "Sat" to Calendar.SATURDAY
    )

    private val dayIndex = mapOf(
        "Sun" to 1,
        "Mon" to 2,
        "Tue" to 3,
        "Wed" to 4,
        "Thu" to 5,
        "Fri" to 6,
        "Sat" to 7
    )

    @SuppressLint("ScheduleExactAlarm")
    fun scheduleTask(task: TaskEntity): Boolean {

        if (!task.notificationEnabled) return true

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                return false
            }
        }

        val selectedDays = task.selectedDays
            .split(",")
            .map { it.trim() }
            .filter { it in dayToCalendar.keys }
            .distinct()

        if (selectedDays.isEmpty()) {
            return false
        }

        cancelTask(task.id)

        selectedDays.forEach { day ->

            val calendarDay = dayToCalendar[day]
                ?: return@forEach

            val requestIndex = dayIndex[day]
                ?: return@forEach

            val startCalendar = getNextOccurrence(
                calendarDay = calendarDay,
                hour = task.startHour,
                minute = task.startMinute
            )

            val reminderCalendar = startCalendar.clone() as Calendar
            reminderCalendar.add(Calendar.MINUTE, -task.reminderMinutes)

            if (reminderCalendar.timeInMillis <= System.currentTimeMillis()) {
                startCalendar.add(Calendar.WEEK_OF_YEAR, 1)
                reminderCalendar.add(Calendar.WEEK_OF_YEAR, 1)
            }

            if (
                task.endDateMillis > 0L &&
                startCalendar.timeInMillis > task.endDateMillis
            ) {
                return@forEach
            }

            val intent = Intent(
                context,
                NotificationReceiver::class.java
            ).apply {

                putExtra("TASK_ID", task.id)

                putExtra("TASK_COLOR", task.color)

                putExtra("TASK_PRIORITY", task.priority)

                putExtra("TASK_DEADLINE_MILLIS", task.deadlineMillis ?: -1L)

                putExtra("TASK_IS_MOVABLE", task.isMovable)

                putExtra(
                    "TASK_TITLE",
                    task.title
                )

                putExtra(
                    "TASK_QUESTION",
                    task.notificationQuestion
                )

                putExtra(
                    "TASK_START_MILLIS",
                    startCalendar.timeInMillis
                )

                putExtra(
                    "TASK_SELECTED_DAYS",
                    task.selectedDays
                )

                putExtra(
                    "TASK_START_HOUR",
                    task.startHour
                )

                putExtra(
                    "TASK_START_MINUTE",
                    task.startMinute
                )

                putExtra(
                    "TASK_END_HOUR",
                    task.endHour
                )

                putExtra(
                    "TASK_END_MINUTE",
                    task.endMinute
                )

                putExtra(
                    "TASK_REMINDER_MINUTES",
                    task.reminderMinutes
                )

                putExtra(
                    "TASK_NOTIFICATION_ENABLED",
                    task.notificationEnabled
                )

                putExtra(
                    "TASK_END_DATE_MILLIS",
                    task.endDateMillis
                )

                putExtra(
                    "TASK_END_DATE_TEXT",
                    task.endDateText
                )
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                getRequestCode(task.id, requestIndex),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                reminderCalendar.timeInMillis,
                pendingIntent
            )
        }

        return true
    }

    fun cancelTask(taskId: Int) {
        WorkManager.getInstance(context)
            .cancelAllWorkByTag("follow_up_$taskId")

        dayIndex.values.forEach { index ->

            val intent = Intent(
                context,
                NotificationReceiver::class.java
            )

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                getRequestCode(taskId, index),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.cancel(pendingIntent)

            pendingIntent.cancel()
        }
    }

    private fun getRequestCode(
        taskId: Int,
        dayIndex: Int
    ): Int {
        return taskId * 10 + dayIndex
    }

    private fun getNextOccurrence(
        calendarDay: Int,
        hour: Int,
        minute: Int
    ): Calendar {

        val now = Calendar.getInstance()

        val calendar = Calendar.getInstance().apply {

            set(
                Calendar.DAY_OF_WEEK,
                calendarDay
            )

            set(
                Calendar.HOUR_OF_DAY,
                hour
            )

            set(
                Calendar.MINUTE,
                minute
            )

            set(
                Calendar.SECOND,
                0
            )

            set(
                Calendar.MILLISECOND,
                0
            )
        }

        if (
            calendar.timeInMillis <=
            now.timeInMillis
        ) {
            calendar.add(
                Calendar.WEEK_OF_YEAR,
                1
            )
        }

        return calendar
    }
}