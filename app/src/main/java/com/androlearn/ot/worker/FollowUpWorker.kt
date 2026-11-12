
package com.androlearn.ot.worker

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.androlearn.ot.R
import com.androlearn.ot.data.local.entity.TaskEntity
import com.androlearn.ot.data.repository.TaskRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class FollowUpWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: TaskRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val taskId = inputData.getInt("TASK_ID", -1)
        val taskDate = inputData.getString("TASK_DATE")

        if (taskId <= 0 || taskDate.isNullOrBlank()) {
            return Result.failure()
        }

        return try {
            val task = repository.getTaskById(taskId)
                ?: return Result.success()

            if (!task.notificationEnabled) {
                return Result.success()
            }

            val completion = repository.getTaskCompletion(
                taskId = taskId,
                date = taskDate
            )

            if (completion?.isCompleted == true) {
                return Result.success()
            }

            val suggestedTime = findFreeTimeSlot(task, taskDate)

            showSuggestionNotification(
                task = task,
                suggestedTime = suggestedTime
            )

            Result.success()

        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e("FollowUpWorker", "Follow-up failed", e)
            Result.retry()
        }
    }

    @SuppressLint("DefaultLocale")
    private suspend fun findFreeTimeSlot(
        task: TaskEntity,
        taskDate: String
    ): String {

        val startDate = parseDate(taskDate)
            ?: return "امروز زمان آزادی پیدا نشد"

        val taskStart = task.startHour * 60 + task.startMinute
        val taskEnd = task.endHour * 60 + task.endMinute

        val crossesMidnight = taskEnd <= taskStart

        val candidateDate = startDate.clone() as Calendar

        if (crossesMidnight) {
            candidateDate.add(Calendar.DAY_OF_YEAR, 1)
        }

        val currentDay = getDayKey(candidateDate)

        val previousDate = candidateDate.clone() as Calendar
        previousDate.add(Calendar.DAY_OF_YEAR, -1)

        val previousDay = getDayKey(previousDate)

        val allTasks = repository.getAllTasks().first()

        val relevantTasks = allTasks.filter { otherTask ->
            if (otherTask.id == task.id) {
                false
            } else {
                val days = otherTask.selectedDays
                    .split(",")
                    .map { it.trim() }

                currentDay in days || previousDay in days
            }
        }

        val intervals = mutableListOf<Pair<Int, Int>>()

        relevantTasks.forEach { otherTask ->

            val days = otherTask.selectedDays
                .split(",")
                .map { it.trim() }

            val otherStart =
                otherTask.startHour * 60 + otherTask.startMinute

            val otherEndRaw =
                otherTask.endHour * 60 + otherTask.endMinute

            val otherCrossesMidnight = otherEndRaw <= otherStart

            // تسک‌های روز جاری
            if (currentDay in days) {
                val end = if (otherCrossesMidnight) {
                    otherEndRaw + 24 * 60
                } else {
                    otherEndRaw
                }

                intervals.add(otherStart to end)
            }

            // تسک‌های روز قبل که وارد روز جاری می‌شوند
            if (previousDay in days && otherCrossesMidnight) {
                intervals.add(
                    (otherStart - 24 * 60) to otherEndRaw
                )
            }
        }

        var candidate = taskEnd + 20

        // جست‌وجوی بازه آزاد ۶۰ دقیقه‌ای تا ساعت ۲۲
        while (candidate >= 0 && candidate + 60 <= 22 * 60) {

            val candidateEnd = candidate + 60

            val overlaps = intervals.any { interval ->
                candidate < interval.second &&
                        candidateEnd > interval.first
            }

            if (!overlaps) {
                return String.format(
                    Locale.US,
                    "%02d:%02d",
                    candidate / 60,
                    candidate % 60
                )
            }

            candidate += 30
        }

        return "امروز زمان آزادی پیدا نشد"
    }

    private fun parseDate(date: String): Calendar? {
        return try {
            val formatter = SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.US
            ).apply {
                isLenient = false
            }

            val parsed = formatter.parse(date)
                ?: return null

            Calendar.getInstance().apply {
                time = parsed
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun getDayKey(calendar: Calendar): String {
        return when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.SUNDAY -> "Sun"
            Calendar.MONDAY -> "Mon"
            Calendar.TUESDAY -> "Tue"
            Calendar.WEDNESDAY -> "Wed"
            Calendar.THURSDAY -> "Thu"
            Calendar.FRIDAY -> "Fri"
            Calendar.SATURDAY -> "Sat"
            else -> ""
        }
    }

    private fun showSuggestionNotification(
        task: TaskEntity,
        suggestedTime: String
    ) {
        val channelId = "suggestions"

        val notificationManager =
            applicationContext.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Task Suggestions",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            notificationManager.createNotificationChannel(channel)
        }

        val text =
            "به نظر می‌رسه هنوز \"${task.title}\" رو انجام ندادی. " +
                    "چطوره ساعت $suggestedTime انجامش بدی؟"

        val notification = NotificationCompat.Builder(
            applicationContext,
            channelId
        )
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("OnTime")
            .setContentText(text)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(text)
            )
            .setAutoCancel(true)
            .build()

        notificationManager.notify(
            task.id + 100000,
            notification
        )
    }
}
