package com.androlearn.ot

import com.androlearn.ot.data.local.entity.TaskEntity
import com.androlearn.ot.domain.recovery.RecoveryEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RecoveryEngineTest {

    private val engine = RecoveryEngine()

    private val nowMinute = 12 * 60
    private val nowMillis = 1_000_000L

    private fun createTask(
        id: Int,
        title: String,
        startHour: Int,
        endHour: Int,
        priority: Int = 2,
        isMovable: Boolean = true,
        deadlineMillis: Long? = null
    ) = TaskEntity(
        id = id,
        title = title,
        startHour = startHour,
        startMinute = 0,
        endHour = endHour,
        endMinute = 0,
        color = 0,
        selectedDays = "Mon",
        priority = priority,
        isMovable = isMovable,
        deadlineMillis = deadlineMillis
    )

    @Test
    fun `overdue task should be moved to current free time`() {

        val task = createTask(
            id = 1,
            title = "مطالعه Kotlin",
            startHour = 9,
            endHour = 10
        )

        val result = engine.generateRecoveryPlan(
            date = "1405/08/10",
            tasks = listOf(task),
            completedTaskIds = emptySet(),
            nowMinute = nowMinute,
            nowMillis = nowMillis
        )

        assertEquals(1, result.suggestions.size)

        val suggestion = result.suggestions.first()

        assertEquals(12 * 60, suggestion.suggestedStartMinute)
        assertEquals(13 * 60, suggestion.suggestedEndMinute)
    }

    @Test
    fun `future task should keep its original time`() {

        val overdueTask = createTask(
            id = 1,
            title = "مطالعه Kotlin",
            startHour = 9,
            endHour = 10
        )

        val futureTask = createTask(
            id = 2,
            title = "کلاس آنلاین",
            startHour = 13,
            endHour = 14
        )

        val result = engine.generateRecoveryPlan(
            date = "1405/08/10",
            tasks = listOf(overdueTask, futureTask),
            completedTaskIds = emptySet(),
            nowMinute = nowMinute,
            nowMillis = nowMillis
        )

        assertEquals(1, result.suggestions.size)

        val suggestion = result.suggestions.first()

        assertEquals(12 * 60, suggestion.suggestedStartMinute)
        assertEquals(13 * 60, suggestion.suggestedEndMinute)
    }

    @Test
    fun `completed task should not be rescheduled`() {

        val task = createTask(
            id = 1,
            title = "تمرین ریاضی",
            startHour = 9,
            endHour = 10
        )

        val result = engine.generateRecoveryPlan(
            date = "1405/08/10",
            tasks = listOf(task),
            completedTaskIds = setOf(1),
            nowMinute = nowMinute,
            nowMillis = nowMillis
        )

        assertTrue(result.suggestions.isEmpty())
        assertTrue(result.unscheduledTasks.isEmpty())
    }

    @Test
    fun `immovable overdue task should not be rescheduled`() {

        val task = createTask(
            id = 1,
            title = "قرار مهم",
            startHour = 9,
            endHour = 10,
            isMovable = false
        )

        val result = engine.generateRecoveryPlan(
            date = "1405/08/10",
            tasks = listOf(task),
            completedTaskIds = emptySet(),
            nowMinute = nowMinute,
            nowMillis = nowMillis
        )

        assertTrue(result.suggestions.isEmpty())
        assertEquals(1, result.unscheduledTasks.size)

        assertEquals(
            "این کار اجازه جابه‌جایی ندارد.",
            result.unscheduledTasks.first().reason
        )
    }

    @Test
    fun `task should not be scheduled after its deadline`() {

        val deadline = nowMillis + 30 * 60_000L

        val task = createTask(
            id = 10,
            title = "تمرین Kotlin",
            startHour = 9,
            endHour = 10,
            deadlineMillis = deadline
        )

        val result = engine.generateRecoveryPlan(
            date = "1405/08/10",
            tasks = listOf(task),
            completedTaskIds = emptySet(),
            nowMinute = nowMinute,
            nowMillis = nowMillis
        )

        assertTrue(result.suggestions.isEmpty())
        assertEquals(1, result.unscheduledTasks.size)
    }

    @Test
    fun `expired deadline should be reported`() {

        val task = createTask(
            id = 11,
            title = "تحویل پروژه",
            startHour = 9,
            endHour = 10,
            deadlineMillis = nowMillis - 60_000L
        )

        val result = engine.generateRecoveryPlan(
            date = "1405/08/10",
            tasks = listOf(task),
            completedTaskIds = emptySet(),
            nowMinute = nowMinute,
            nowMillis = nowMillis
        )

        assertTrue(result.suggestions.isEmpty())

        assertEquals(
            "مهلت انجام این کار گذشته است.",
            result.unscheduledTasks.first().reason
        )
    }
}