package com.androlearn.ot.data.local.entity

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "task_completions",
    primaryKeys = ["taskId", "date"],
    indices = [Index(value = ["date"])]
)
data class TaskCompletionEntity(
    val taskId: Int,
    val date: String,
    val isCompleted: Boolean = false
)