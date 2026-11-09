package com.androlearn.ot.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androlearn.ot.data.local.entity.TaskCompletionEntity
import com.androlearn.ot.data.local.entity.TaskEntity
import com.androlearn.ot.data.repository.TaskRepository
import com.androlearn.ot.util.AlarmScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TaskViewModel @Inject constructor(
    private val repository: TaskRepository,
    private val alarmScheduler: AlarmScheduler
) : ViewModel() {

    private val days = listOf(
        "Sun",
        "Mon",
        "Tue",
        "Wed",
        "Thu",
        "Fri",
        "Sat"
    )

    private val _selectedDay =
        MutableStateFlow(getCurrentDay())

    val selectedDay: StateFlow<String> =
        _selectedDay

    val tasks: StateFlow<List<TaskEntity>> =
        _selectedDay
            .flatMapLatest { day ->
                repository.getTasksByDay(day)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun completionsForDate(date: String) =
        repository.getCompletionsForDate(date)

    fun setSelectedDay(day: String) {
        _selectedDay.value = day
    }

    private fun getCurrentDay(): String {
        val calendar = Calendar.getInstance()
        val dayOfWeek =
            calendar.get(Calendar.DAY_OF_WEEK)

        return days[dayOfWeek - 1]
    }

    fun addTask(
        task: TaskEntity,
        isPro: Boolean
    ) {
        viewModelScope.launch {
            val id = repository.insertTask(
                task = task,
                isPro = isPro
            )

            val savedTask = task.copy(
                id = id.toInt()
            )

            if (savedTask.notificationEnabled) {
                alarmScheduler.scheduleTask(savedTask)
            }
        }
    }

    fun toggleTaskCompletion(
        task: TaskEntity,
        date: String
    ) {
        viewModelScope.launch {
            val current = repository.getTaskCompletion(
                taskId = task.id,
                date = date
            )

            repository.saveTaskCompletion(
                TaskCompletionEntity(
                    taskId = task.id,
                    date = date,
                    isCompleted = !(current?.isCompleted ?: false)
                )
            )
        }
    }
    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {

            alarmScheduler.cancelTask(task.id)

            repository.deleteTask(task)
        }
    }
}