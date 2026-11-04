package com.androlearn.ot.worker

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.androlearn.ot.R
import com.androlearn.ot.data.local.entity.TaskEntity
import com.androlearn.ot.data.repository.TaskRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.util.Calendar

class FollowUpWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: TaskRepository
) : CoroutineWorker(
    context,
    workerParams
) {

    override suspend fun doWork(): Result {

        val taskId =
            inputData.getInt(
                "TASK_ID",
                -1
            )

        if (taskId == -1) {
            return Result.failure()
        }

        val task =
            repository.getTaskById(taskId)
                ?: return Result.success()

        if (task.isCompleted) {
            return Result.success()
        }

        val suggestedTime =
            findFreeTimeSlot(task)

        showSuggestionNotification(
            task = task,
            suggestedTime = suggestedTime
        )

        return Result.success()
    }

    @SuppressLint("DefaultLocale")
    private suspend fun findFreeTimeSlot(
        task: TaskEntity
    ): String {

        val allTasks =
            repository.getAllTasks().first()

        val currentDay =
            getCurrentDay()

        val todayTasks =
            allTasks.filter { otherTask ->

                otherTask.id != task.id &&
                        otherTask.selectedDays
                            .split(",")
                            .map { it.trim() }
                            .contains(currentDay)
            }

        val startAfter =
            task.endHour * 60 +
                    task.endMinute +
                    20

        var candidate =
            startAfter

        while (candidate < 22 * 60) {

            val candidateEnd =
                candidate + 60

            val overlaps =
                todayTasks.any { otherTask ->

                    val otherStart =
                        otherTask.startHour * 60 +
                                otherTask.startMinute

                    val otherEnd =
                        otherTask.endHour * 60 +
                                otherTask.endMinute

                    candidate < otherEnd &&
                            candidateEnd > otherStart
                }

            if (!overlaps) {

                val hour =
                    candidate / 60

                val minute =
                    candidate % 60

                return String.format(
                    "%02d:%02d",
                    hour,
                    minute
                )
            }

            candidate += 30
        }

        return "فردا"
    }

    private fun getCurrentDay(): String {

        val days =
            listOf(
                "Sun",
                "Mon",
                "Tue",
                "Wed",
                "Thu",
                "Fri",
                "Sat"
            )

        val calendar =
            Calendar.getInstance()

        return days[
            calendar.get(
                Calendar.DAY_OF_WEEK
            ) - 1
        ]
    }

    @SuppressLint("ObsoleteSdkInt")
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
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.O
        ) {

            val channel =
                NotificationChannel(
                    channelId,
                    "Task Suggestions",
                    NotificationManager.IMPORTANCE_DEFAULT
                )

            notificationManager
                .createNotificationChannel(channel)
        }

        val text =
            "به نظر می‌رسه هنوز \"${task.title}\" رو انجام ندادی. " +
                    "چطوره ساعت $suggestedTime انجامش بدی؟"

        val notification =
            NotificationCompat.Builder(
                applicationContext,
                channelId
            )
                .setSmallIcon(
                    R.drawable.ic_launcher_foreground
                )
                .setContentTitle(
                    "OnTime"
                )
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