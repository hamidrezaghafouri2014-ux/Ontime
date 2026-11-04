    package com.androlearn.ot.viewmodel

    import androidx.compose.runtime.getValue
    import androidx.compose.runtime.mutableIntStateOf
    import androidx.compose.runtime.mutableStateOf
    import androidx.compose.runtime.setValue
    import androidx.lifecycle.ViewModel
    import androidx.lifecycle.viewModelScope
    import com.androlearn.ot.data.local.entity.TaskEntity
    import com.androlearn.ot.data.repository.TaskRepository
    import com.androlearn.ot.ui.theme.Primary
    import com.androlearn.ot.util.AlarmScheduler
    import dagger.hilt.android.lifecycle.HiltViewModel
    import kotlinx.coroutines.launch
    import javax.inject.Inject

    @HiltViewModel
    class AddTaskViewModel @Inject constructor(
        private val repository: TaskRepository,
        private val alarmScheduler: AlarmScheduler
    ) : ViewModel() {

        var timeError by mutableStateOf<String?>(null)
            private set



        var title by mutableStateOf("")

        /**
         * روزهای انتخاب‌شده
         *
         * مثال:
         * ["Sat", "Mon", "Wed"]
         */
        var selectedDays by mutableStateOf(setOf("Sat"))

        var startTime by mutableStateOf("")

        var endTime by mutableStateOf("")

        var notificationEnabled by mutableStateOf(true)

        var question by mutableStateOf("کارتو انجام دادی؟")

        var reminderMinutes by mutableStateOf("0")

        var endDateMillis by mutableStateOf(0L)

        var endDateText by mutableStateOf("")

        var color by mutableIntStateOf(Primary.value.toInt())

        fun toggleDay(day: String) {
            selectedDays = if (selectedDays.contains(day)) {
                selectedDays - day
            } else {
                selectedDays + day
            }
        }

        fun saveTask(onSuccess: () -> Unit) {

            if (title.isBlank()) return

            if (selectedDays.isEmpty()) return

            val startParts = startTime.split(":")
            val endParts = endTime.split(":")

            val reminder =
                reminderMinutes.toIntOrNull()?.coerceAtLeast(0) ?: 0

            timeError = null

            if (title.isBlank() || selectedDays.isEmpty()) return

            fun parseTime(value: String): Pair<Int, Int>? {
                val parts = value.split(":")
                if (parts.size != 2) return null

                val hour = parts[0].toIntOrNull() ?: return null
                val minute = parts[1].toIntOrNull() ?: return null

                if (hour !in 0..23 || minute !in 0..59) return null

                return hour to minute
            }

            val start = parseTime(startTime)
            val end = parseTime(endTime)

            if (start == null || end == null) {
                timeError = "ساعت شروع یا پایان معتبر نیست."
                return
            }

            val (startHour, startMin) = start
            val (endHour, endMin) = end

            if (endHour * 60 + endMin <= startHour * 60 + startMin) {
                timeError = "ساعت پایان باید بعد از ساعت شروع باشد."
                return
            }

            val task = TaskEntity(
                title = title,

                startHour = startHour,
                startMinute = startMin,

                endHour = endHour,
                endMinute = endMin,

                color = color,

                selectedDays = selectedDays.joinToString(","),

                endDateMillis = endDateMillis,

                endDateText = endDateText,

                notificationEnabled = notificationEnabled,

                notificationQuestion = question,

                reminderMinutes = reminder
            )

            viewModelScope.launch {
                try {
                    val id = repository.insertTask(task)

                    if (id <= 0L) {
                        timeError = "ذخیره تسک در دیتابیس ناموفق بود."
                        return@launch
                    }

                    val savedTask = task.copy(id = id.toInt())

                    onSuccess()

                    if (notificationEnabled) {
                        try {
                            alarmScheduler.scheduleTask(savedTask)
                        } catch (e: Exception) {
                            android.util.Log.e(
                                "AddTaskViewModel",
                                "Task saved, but alarm scheduling failed",
                                e
                            )
                        }
                    }

                } catch (e: Exception) {
                    android.util.Log.e(
                        "AddTaskViewModel",
                        "Task saving failed",
                        e
                    )

                    timeError = "ذخیره تسک ناموفق بود: ${e.message}"
                }
            }
        }
    }