package com.androlearn.ot.data.repository

import com.androlearn.ot.domain.recovery.RecoveryEngine
import com.androlearn.ot.domain.recovery.RecoveryResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecoveryRepository @Inject constructor(
    private val taskRepository: TaskRepository,
    private val recoveryEngine: RecoveryEngine
) {

    suspend fun generateRecoveryPlan(
        date: String,
        day: String,
        nowMinute: Int,
        nowMillis: Long,
        dayEndMinute: Int = 22 * 60
    ): RecoveryResult {

        // دریافت کارهای تکرارشونده این روز
        val tasks = taskRepository.getTasksByDayOnce(day)

        // دریافت وضعیت تکمیل کارها در تاریخ موردنظر
        val completions = taskRepository
            .getTaskCompletionsForDateOnce(date)

        val completedTaskIds = completions
            .filter { it.isCompleted }
            .map { it.taskId }
            .toSet()

        return recoveryEngine.generateRecoveryPlan(
            date = date,
            tasks = tasks,
            completedTaskIds = completedTaskIds,
            nowMinute = nowMinute,
            nowMillis = nowMillis,
            dayEndMinute = dayEndMinute
        )
    }
}