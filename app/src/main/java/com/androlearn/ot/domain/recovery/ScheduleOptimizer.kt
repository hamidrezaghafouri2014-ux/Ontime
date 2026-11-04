package com.androlearn.ot.domain.recovery

import com.androlearn.ot.data.local.entity.TaskEntity
import java.util.Calendar
import javax.inject.Inject

class ScheduleOptimizer @Inject constructor(
    private val priorityCalculator: TaskPriorityCalculator
) {

    private data class TimeSlot(
        val start: Int,
        val end: Int
    )

    fun optimize(
        date: String,
        tasks: List<TaskEntity>,
        completedTaskIds: Set<Int>,
        nowMinute: Int,
        nowMillis: Long,
        dayEndMinute: Int = 22 * 60
    ): RecoveryResult {

        val currentMinute = nowMinute.coerceIn(0, dayEndMinute)

        val suggestions = mutableListOf<RecoverySuggestion>()
        val unscheduled = mutableListOf<UnscheduledTask>()

        val pendingTasks = tasks.filter {
            it.id !in completedTaskIds
        }

        val overdueTasks = pendingTasks.filter {
            getEndMinute(it) <= currentMinute
        }

        val fixedTasks = pendingTasks.filter {
            getEndMinute(it) > currentMinute
        }

        val occupiedSlots = fixedTasks.mapNotNull { task ->
            val start = getStartMinute(task)
            val end = getEndMinute(task)

            val clippedStart = maxOf(start, currentMinute)
            val clippedEnd = minOf(end, dayEndMinute)

            if (clippedStart < clippedEnd) {
                TimeSlot(clippedStart, clippedEnd)
            } else {
                null
            }
        }.toMutableList()

        val availableMinutes = calculateFreeMinutes(
            currentMinute,
            dayEndMinute,
            occupiedSlots
        )

        overdueTasks
            .filter { !it.isMovable }
            .forEach { task ->
                unscheduled.add(
                    UnscheduledTask(
                        taskId = task.id,
                        taskTitle = task.title,
                        reason = "این کار اجازه جابه‌جایی ندارد."
                    )
                )
            }

        val movableTasks = overdueTasks
            .filter { it.isMovable }
            .sortedByDescending {
                priorityCalculator.calculate(it, nowMillis)
            }

        for (task in movableTasks) {

            val originalStart = getStartMinute(task)
            val originalEnd = getEndMinute(task)
            val duration = originalEnd - originalStart

            if (duration <= 0) {
                unscheduled.add(
                    UnscheduledTask(
                        taskId = task.id,
                        taskTitle = task.title,
                        reason = "مدت زمان این کار معتبر نیست."
                    )
                )
                continue
            }

            val deadline = task.deadlineMillis

            if (deadline != null && deadline <= nowMillis) {
                unscheduled.add(
                    UnscheduledTask(
                        taskId = task.id,
                        taskTitle = task.title,
                        reason = "مهلت انجام این کار گذشته است."
                    )
                )
                continue
            }

            val latestEndMinute = getLatestEndMinute(
                deadlineMillis = deadline,
                nowMillis = nowMillis,
                dayEndMinute = dayEndMinute
            )

            val suggestedStart = findAvailableSlot(
                duration = duration,
                currentMinute = currentMinute,
                dayEndMinute = dayEndMinute,
                latestEndMinute = latestEndMinute,
                occupiedSlots = occupiedSlots
            )

            if (suggestedStart == null) {
                unscheduled.add(
                    UnscheduledTask(
                        taskId = task.id,
                        taskTitle = task.title,
                        reason = if (latestEndMinute < dayEndMinute) {
                            "تا قبل از مهلت تعیین‌شده، زمان آزاد کافی وجود ندارد."
                        } else {
                            "زمان آزاد کافی برای این کار وجود ندارد."
                        }
                    )
                )
                continue
            }

            val suggestedEnd = suggestedStart + duration

            val score = priorityCalculator.calculate(
                task = task,
                nowMillis = nowMillis
            )

            suggestions.add(
                RecoverySuggestion(
                    taskId = task.id,
                    taskTitle = task.title,
                    originalStartMinute = originalStart,
                    originalEndMinute = originalEnd,
                    suggestedStartMinute = suggestedStart,
                    suggestedEndMinute = suggestedEnd,
                    priorityScore = score,
                    reason = getSuggestionReason(score)
                )
            )

            occupiedSlots.add(
                TimeSlot(suggestedStart, suggestedEnd)
            )

            occupiedSlots.sortBy { it.start }
        }

        return RecoveryResult(
            date = date,
            generatedAtMillis = nowMillis,
            availableMinutes = availableMinutes,
            suggestions = suggestions,
            unscheduledTasks = unscheduled
        )
    }

    private fun findAvailableSlot(
        duration: Int,
        currentMinute: Int,
        dayEndMinute: Int,
        latestEndMinute: Int,
        occupiedSlots: List<TimeSlot>
    ): Int? {

        val effectiveDayEnd = minOf(dayEndMinute, latestEndMinute)

        if (effectiveDayEnd - currentMinute < duration) {
            return null
        }

        val sortedSlots = occupiedSlots.sortedBy { it.start }
        var candidateStart = currentMinute

        for (slot in sortedSlots) {

            if (slot.end <= candidateStart) continue

            if (slot.start - candidateStart >= duration) {
                return candidateStart
            }

            candidateStart = maxOf(candidateStart, slot.end)

            if (candidateStart + duration > effectiveDayEnd) {
                return null
            }
        }

        return if (effectiveDayEnd - candidateStart >= duration) {
            candidateStart
        } else {
            null
        }
    }

    private fun getLatestEndMinute(
        deadlineMillis: Long?,
        nowMillis: Long,
        dayEndMinute: Int
    ): Int {

        if (deadlineMillis == null) return dayEndMinute

        val nowCalendar = Calendar.getInstance().apply {
            timeInMillis = nowMillis
        }

        val deadlineCalendar = Calendar.getInstance().apply {
            timeInMillis = deadlineMillis
        }

        val sameDay =
            nowCalendar.get(Calendar.YEAR) ==
                    deadlineCalendar.get(Calendar.YEAR) &&
                    nowCalendar.get(Calendar.DAY_OF_YEAR) ==
                    deadlineCalendar.get(Calendar.DAY_OF_YEAR)

        if (!sameDay) return dayEndMinute

        val deadlineMinute =
            deadlineCalendar.get(Calendar.HOUR_OF_DAY) * 60 +
                    deadlineCalendar.get(Calendar.MINUTE)

        return minOf(deadlineMinute, dayEndMinute)
    }

    private fun calculateFreeMinutes(
        currentMinute: Int,
        dayEndMinute: Int,
        occupiedSlots: List<TimeSlot>
    ): Int {

        val mergedSlots = mergeSlots(occupiedSlots)

        val occupiedMinutes = mergedSlots.sumOf {
            it.end - it.start
        }

        return (
                dayEndMinute - currentMinute - occupiedMinutes
                ).coerceAtLeast(0)
    }

    private fun mergeSlots(
        slots: List<TimeSlot>
    ): List<TimeSlot> {

        if (slots.isEmpty()) return emptyList()

        val sorted = slots.sortedBy { it.start }
        val merged = mutableListOf<TimeSlot>()

        var current = sorted.first()

        for (next in sorted.drop(1)) {
            if (next.start <= current.end) {
                current = TimeSlot(
                    start = current.start,
                    end = maxOf(current.end, next.end)
                )
            } else {
                merged.add(current)
                current = next
            }
        }

        merged.add(current)
        return merged
    }

    private fun getStartMinute(task: TaskEntity): Int =
        task.startHour * 60 + task.startMinute

    private fun getEndMinute(task: TaskEntity): Int =
        task.endHour * 60 + task.endMinute

    private fun getSuggestionReason(score: Int): String =
        when {
            score >= 75 ->
                "اولویت بالایی دارد و بهتر است زودتر انجام شود."

            score >= 50 ->
                "با توجه به اهمیت و مهلت، در این زمان قرار گرفته است."

            else ->
                "در زمان آزاد باقی‌مانده قرار گرفته است."
        }
}