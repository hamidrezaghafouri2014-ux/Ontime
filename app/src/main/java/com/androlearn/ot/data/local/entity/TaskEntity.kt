package com.androlearn.ot.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val title: String,

    val startHour: Int,
    val startMinute: Int,

    val endHour: Int,
    val endMinute: Int,

    val color: Int,

    val selectedDays: String,

    val endDateMillis: Long = 0L,

    val endDateText: String = "",

    val notificationEnabled: Boolean = true,

    val notificationQuestion: String = "کارتو انجام دادی؟",

    val reminderMinutes: Int = 0,

    val isCompleted: Boolean = false,

    // Smart Recovery
    val priority: Int = 2,
    val deadlineMillis: Long? = null,
    val isMovable: Boolean = true
)