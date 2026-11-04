package com.androlearn.ot.domain.recovery

data class RecoveryResult(
    val date: String,
    val generatedAtMillis: Long,
    val availableMinutes: Int,
    val suggestions: List<RecoverySuggestion>,
    val unscheduledTasks: List<UnscheduledTask>
)

data class RecoverySuggestion(
    val taskId: Int,
    val taskTitle: String,
    val originalStartMinute: Int,
    val originalEndMinute: Int,
    val suggestedStartMinute: Int,
    val suggestedEndMinute: Int,
    val priorityScore: Int,
    val reason: String
)

data class UnscheduledTask(
    val taskId: Int,
    val taskTitle: String,
    val reason: String
)