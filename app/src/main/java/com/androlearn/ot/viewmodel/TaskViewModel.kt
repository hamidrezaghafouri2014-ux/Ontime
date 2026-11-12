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

    private val _timerTask = MutableStateFlow<TaskEntity?>(null)
    val timerTask: StateFlow<TaskEntity?> = _timerTask

    fun completionsForDate(date: String) =
        repository.getCompletionsForDate(date)

    fun setSelectedDay(day: String) {
        _selectedDay.value = day
    }

    fun loadOrStartTimer(taskId: Int) {
        viewModelScope.launch {
            val task = repository.getTaskById(taskId) ?: run {
                _timerTask.value = null
                return@launch
            }

            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            val todayStart = calendar.timeInMillis

            val shouldStart =
                task.timerEndMillis == 0L ||
                        task.timerEndMillis < todayStart

            val updatedTask = if (shouldStart) {
                val start = task.startHour * 60L + task.startMinute
                val end = task.endHour * 60L + task.endMinute

                val durationMillis = (end - start) * 60_000L

                task.copy(
                    timerEndMillis = System.currentTimeMillis() +
                            durationMillis.coerceAtLeast(0L)
                )
            } else {
                task
            }

            if (updatedTask != task) {
                repository.updateTask(updatedTask)
            }

            _timerTask.value = updatedTask
        }
    }

    fun completeTimer(
        taskId: Int,
        date: String,
        onCompleted: () -> Unit
    ) {
        viewModelScope.launch {
            val task = repository.getTaskById(taskId)
                ?: return@launch

            repository.saveTaskCompletion(
                TaskCompletionEntity(
                    taskId = taskId,
                    date = date,
                    isCompleted = true
                )
            )

            val updatedTask = task.copy(
                timerEndMillis = System.currentTimeMillis()
            )

            repository.updateTask(updatedTask)
            _timerTask.value = updatedTask

            onCompleted()
        }
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

            if (id <= 0L) return@launch

            val savedTask = task.copy(
                id = id.toInt()
            )

            if (savedTask.notificationEnabled) {
                val scheduled = alarmScheduler.scheduleTask(savedTask)

                if (!scheduled) {
                    // زمان‌بندی اعلان موفق نشد
                    return@launch
                }
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

    fun updateTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.updateTask(task)

            if (task.notificationEnabled) {
                alarmScheduler.scheduleTask(task)
            } else {
                alarmScheduler.cancelTask(task.id)
            }
        }
    }
    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {

            alarmScheduler.cancelTask(task.id)

            repository.deleteTask(task)
        }
    }
}