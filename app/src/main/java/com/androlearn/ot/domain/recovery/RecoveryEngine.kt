package com.androlearn.ot.domain.recovery

import com.androlearn.ot.data.local.entity.TaskEntity
import jakarta.inject.Inject

class RecoveryEngine @Inject constructor(
    private val scheduleOptimizer: ScheduleOptimizer
) {

    fun generateRecoveryPlan(
        date: String,
        tasks: List<TaskEntity>,
        completedTaskIds: Set<Int>,
        nowMinute: Int,
        nowMillis: Long,
        dayEndMinute: Int = 22 * 60
    ): RecoveryResult {

        require(date.isNotBlank()) {
            "تاریخ نمی‌تواند خالی باشد."
        }

        require(nowMinute in 0..1439) {
            "زمان فعلی معتبر نیست."
        }

        require(dayEndMinute in 1..1440) {
            "پایان روز معتبر نیست."
        }

        require(dayEndMinute > nowMinute) {
            "زمان پایان روز باید بعد از زمان فعلی باشد."
        }

        val uniqueTasks = tasks.distinctBy { it.id }

        val pendingTasks = uniqueTasks.filter {
            it.id !in completedTaskIds
        }

        val invalidTasks = pendingTasks.filterNot {
            isValidTask(it)
        }

        val validTasks = pendingTasks.filter {
            isValidTask(it)
        }

        val result = scheduleOptimizer.optimize(
            date = date,
            tasks = validTasks,
            completedTaskIds = emptySet(),
            nowMinute = nowMinute,
            nowMillis = nowMillis,
            dayEndMinute = dayEndMinute
        )

        val invalidResults = invalidTasks.map { task ->
            UnscheduledTask(
                taskId = task.id,
                taskTitle = task.title,
                reason = getInvalidReason(task)
            )
        }

        return result.copy(
            unscheduledTasks = result.unscheduledTasks + invalidResults
        )
    }

    private fun isValidTask(task: TaskEntity): Boolean {

        val validStart =
            task.startHour in 0..23 &&
                    task.startMinute in 0..59

        val validEnd =
            task.endHour in 0..23 &&
                    task.endMinute in 0..59

        val validPriority = task.priority in 1..3

        val startMinute =
            task.startHour * 60 + task.startMinute

        val endMinute =
            task.endHour * 60 + task.endMinute

        return validStart &&
                validEnd &&
                validPriority &&
                endMinute > startMinute
    }

    private fun getInvalidReason(task: TaskEntity): String {

        val startValid =
            task.startHour in 0..23 &&
                    task.startMinute in 0..59

        val endValid =
            task.endHour in 0..23 &&
                    task.endMinute in 0..59

        val startMinute =
            task.startHour * 60 + task.startMinute

        val endMinute =
            task.endHour * 60 + task.endMinute

        return when {
            !startValid ->
                "زمان شروع این کار معتبر نیست."

            !endValid ->
                "زمان پایان این کار معتبر نیست."

            endMinute <= startMinute ->
                "مدت زمان این کار معتبر نیست."

            task.priority !in 1..3 ->
                "اولویت این کار معتبر نیست."

            else ->
                "اطلاعات این کار معتبر نیست."
        }
    }
}