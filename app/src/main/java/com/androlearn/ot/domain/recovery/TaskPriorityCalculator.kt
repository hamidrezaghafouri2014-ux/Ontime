package com.androlearn.ot.domain.recovery

import com.androlearn.ot.data.local.entity.TaskEntity
import javax.inject.Inject


class TaskPriorityCalculator @Inject constructor() {

    fun calculate(
        task: TaskEntity,
        nowMillis: Long
    ): Int {
        val importanceScore = calculateImportance(task.priority)

        val urgencyScore = calculateUrgency(
            deadlineMillis = task.deadlineMillis,
            nowMillis = nowMillis
        )

        val feasibilityScore = calculateFeasibility(task)

        return (importanceScore + urgencyScore + feasibilityScore)
            .coerceIn(0, 100)
    }

    private fun calculateImportance(priority: Int): Int {
        return when (priority) {
            1 -> 15
            2 -> 27
            3 -> 40
            else -> 27
        }
    }

    private fun calculateUrgency(
        deadlineMillis: Long?,
        nowMillis: Long
    ): Int {
        if (deadlineMillis == null) return 0

        val remainingMillis = deadlineMillis - nowMillis
        val remainingMinutes = remainingMillis / 60_000L

        return when {
            remainingMinutes <= 0 -> 35
            remainingMinutes <= 60 -> 35
            remainingMinutes <= 180 -> 28
            remainingMinutes <= 360 -> 20
            else -> 10
        }
    }

    private fun calculateFeasibility(
        task: TaskEntity
    ): Int {
        val duration = getDurationMinutes(task)

        return when {
            duration <= 30 -> 25
            duration <= 60 -> 20
            duration <= 120 -> 15
            else -> 10
        }
    }

    private fun getDurationMinutes(
        task: TaskEntity
    ): Int {
        val start = task.startHour * 60 + task.startMinute
        val end = task.endHour * 60 + task.endMinute

        return (end - start).coerceAtLeast(0)
    }
}