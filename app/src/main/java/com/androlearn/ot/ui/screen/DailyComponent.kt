package com.androlearn.ot.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.androlearn.ot.data.local.entity.TaskEntity
import com.androlearn.ot.navigation.Screen
import com.androlearn.ot.util.JalaliCalendar
import com.androlearn.ot.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class DateItem(
    val calendar: Calendar,
    val date: JalaliCalendar.JalaliDate,
    val dayName: String,
    val dayCode: String,
    val isToday: Boolean
)

data class DailyTask(
    val id: Int,
    val title: String,
    val startHour: Int,
    val startMinute: Int,
    val endHour: Int,
    val endMinute: Int,
    val color: Color,
    val isCompleted: Boolean,
    val originalEntity: TaskEntity
)

@Composable
fun DailyComponent(
    navController: NavHostController,
    paddingValues: PaddingValues,
    viewModel: TaskViewModel = hiltViewModel()
) {
    val dbTasks by viewModel.tasks.collectAsStateWithLifecycle()

    val today = remember {
        Calendar.getInstance()
    }

    val dateItems = remember {
        val result = mutableListOf<DateItem>()

        val dayNames = mapOf(
            Calendar.SATURDAY to ("شنبه" to "Sat"),
            Calendar.SUNDAY to ("یکشنبه" to "Sun"),
            Calendar.MONDAY to ("دوشنبه" to "Mon"),
            Calendar.TUESDAY to ("سه‌شنبه" to "Tue"),
            Calendar.WEDNESDAY to ("چهارشنبه" to "Wed"),
            Calendar.THURSDAY to ("پنج‌شنبه" to "Thu"),
            Calendar.FRIDAY to ("جمعه" to "Fri")
        )

        for (offset in -30..30) {

            val calendar = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, offset)
            }

            val jalaliDate =
                JalaliCalendar.fromGregorian(calendar)

            val dayInfo =
                dayNames[calendar.get(Calendar.DAY_OF_WEEK)]
                    ?: ("روز" to "Sun")

            val isToday =
                calendar.get(Calendar.YEAR) ==
                        today.get(Calendar.YEAR) &&
                        calendar.get(Calendar.DAY_OF_YEAR) ==
                        today.get(Calendar.DAY_OF_YEAR)

            result.add(
                DateItem(
                    calendar = calendar,
                    date = jalaliDate,
                    dayName = dayInfo.first,
                    dayCode = dayInfo.second,
                    isToday = isToday
                )
            )
        }

        result
    }

    var selectedDate by remember {
        mutableStateOf(
            dateItems.firstOrNull { it.isToday }
                ?: dateItems[30]
        )
    }

    val dateListState =
        rememberLazyListState(
            initialFirstVisibleItemIndex = 30
        )

    LaunchedEffect(Unit) {

        val todayIndex =
            dateItems.indexOfFirst { it.isToday }

        if (todayIndex >= 0) {
            dateListState.scrollToItem(
                todayIndex
            )
        }
    }

    val currentJalaliDate =
        selectedDate.date

    val dateKey = remember(selectedDate.calendar.timeInMillis) {
        SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.US
        ).format(selectedDate.calendar.time)
    }

    val completionStates by viewModel
        .completionsForDate(dateKey)
        .collectAsStateWithLifecycle(
            initialValue = emptyList()
        )

    val dailyTasks = dbTasks.map { task ->
        DailyTask(
            id = task.id,
            title = task.title,
            startHour = task.startHour,
            startMinute = task.startMinute,
            endHour = task.endHour,
            endMinute = task.endMinute,
            color = Color(task.color),
            isCompleted = completionStates.any {
                it.taskId == task.id && it.isCompleted
            },
            originalEntity = task
        )
    }

    val currentMinute = remember {
        val calendar = Calendar.getInstance()
        calendar.get(Calendar.HOUR_OF_DAY) * 60 +
                calendar.get(Calendar.MINUTE)
    }

    val overdueTasks = dailyTasks.filter { task ->
        !task.isCompleted &&
                task.endHour * 60 + task.endMinute <= currentMinute
    }

    val showRecovery = selectedDate.isToday &&
            overdueTasks.isNotEmpty()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.background
            )
    ) {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            /*
             * HEADER
             */

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 20.dp,
                        vertical = 16.dp
                    )
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.SpaceBetween,
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Column {

                        Text(
                            text = "برنامه روزانه",
                            style =
                                MaterialTheme.typography
                                    .headlineSmall,
                            color =
                                MaterialTheme.colorScheme
                                    .onBackground
                        )

                        Spacer(
                            modifier =
                                Modifier.height(4.dp)
                        )

                        Text(
                            text =
                                "${currentJalaliDate.day} " +
                                        "${currentJalaliDate.monthName} " +
                                        "${currentJalaliDate.year}",
                            color =
                                MaterialTheme.colorScheme
                                    .onSurfaceVariant,
                            fontSize = 14.sp
                        )
                    }



                    Icon(
                        imageVector =
                            Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint =
                            MaterialTheme.colorScheme.primary,
                        modifier =
                            Modifier.size(28.dp)
                    )
                }
            }

            /*
             * DATE SELECTOR
             */

            LazyRow(
                state = dateListState,
                modifier = Modifier.fillMaxWidth(),
                contentPadding =
                    PaddingValues(horizontal = 16.dp),
                horizontalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {

                items(
                    items = dateItems,
                    key = {
                        it.calendar.timeInMillis
                    }
                ) { item ->

                    val isSelected =
                        selectedDate.calendar
                            .timeInMillis ==
                                item.calendar.timeInMillis

                    DateCard(
                        item = item,
                        selected = isSelected,
                        onClick = {

                            selectedDate = item

                            viewModel.setSelectedDay(
                                item.dayCode
                            )
                        }
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            HorizontalDivider(
                color =
                    MaterialTheme.colorScheme
                        .surfaceVariant
            )

            if (showRecovery) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "${overdueTasks.size} کار عقب‌افتاده داری",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )

                            Spacer(Modifier.height(4.dp))

                            Text(
                                text = "برنامه‌ات را با زمان باقی‌مانده تنظیم کن.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }

                        TextButton(
                            onClick = {
                                navController.navigate(
                                    Screen.SmartRecovery.createRoute(
                                        date = dateKey,
                                        day = selectedDate.dayCode
                                    )
                                )
                            }
                        ) {
                            Text("تنظیم مجدد")
                        }
                    }
                }
            }

            /*
             * TIMELINE
             */

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding =
                    PaddingValues(
                        top = 16.dp,
                        bottom = 100.dp
                    )
            ) {

                item {

                    Timeline(
                        tasks = dailyTasks,
                        onToggle = { task ->

                            viewModel.toggleTaskCompletion(
                                task.originalEntity,
                                dateKey
                            )
                        },
                        onDelete = { task ->

                            viewModel.deleteTask(
                                task.originalEntity
                            )
                        }
                    )
                }
            }
        }

        /*
         * FAB
         */

        FloatingActionButton(
            onClick = {

                navController.navigate(
                    Screen.AddTask.route
                )
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp),
            containerColor =
                MaterialTheme.colorScheme.primary,
            contentColor =
                MaterialTheme.colorScheme.onPrimary
        ) {

            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "افزودن کار"
            )
        }
    }
}


@Composable
private fun DateCard(
    item: DateItem,
    selected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surface
    }

    val textColor = if (selected) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Card(
        modifier = Modifier
            .width(68.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (selected) 3.dp else 0.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 13.dp, horizontal = 5.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            Text(
                text = item.dayName,
                color = textColor.copy(alpha = if (selected) 1f else 0.7f),
                fontSize = 11.sp
            )

            Text(
                text = item.date.day.toString(),
                color = textColor,
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold
            )

            Box(
                modifier = Modifier
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(
                        if (item.isToday) {
                            if (selected) Color.White else MaterialTheme.colorScheme.primary
                        } else {
                            Color.Transparent
                        }
                    )
            )
        }
    }
}


@Composable
private fun Timeline(
    tasks: List<DailyTask>,
    onToggle: (DailyTask) -> Unit,
    onDelete: (DailyTask) -> Unit
) {
    val startHour = 6
    val endHour = 22
    val hourHeight = 80.dp

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        for (hour in startHour..endHour) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(hourHeight)
            ) {

                Text(
                    text =
                        String.format(
                            "%02d:00",
                            hour
                        ),
                    modifier = Modifier
                        .width(55.dp)
                        .padding(start = 16.dp),
                    color =
                        MaterialTheme.colorScheme
                            .onSurfaceVariant,
                    fontSize = 12.sp
                )

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 65.dp,
                            end = 16.dp
                        )
                        .offset(y = 1.dp),
                    color =
                        MaterialTheme.colorScheme
                            .surfaceVariant
                )
            }
        }

        tasks.forEach { task ->

            val topOffset =
                (
                        (task.startHour - startHour) * 60 +
                                task.startMinute
                        ) * (80f / 60f)

            val duration =
                (
                        (task.endHour * 60 +
                                task.endMinute) -
                                (task.startHour * 60 +
                                        task.startMinute)
                        ).coerceAtLeast(30)

            val taskHeight =
                duration * (80f / 60f)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 70.dp,
                        end = 16.dp
                    )
                    .offset(
                        y = topOffset.dp
                    )
            ) {

                TaskCard(
                    task = task,
                    height = taskHeight.dp,
                    onToggle = {
                        onToggle(task)
                    },
                    onDelete = {
                        onDelete(task)
                    }
                )
            }
        }
    }
}


@Composable
private fun TaskCard(
    task: DailyTask,
    height: Dp,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    val cardColor = if (task.isCompleted) {
        MaterialTheme.colorScheme.surfaceVariant
    } else {
        MaterialTheme.colorScheme.surface
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(height.coerceAtLeast(76.dp)),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .height(44.dp)
                    .clip(RoundedCornerShape(50))
                    .background(task.color)
            )

            Spacer(Modifier.width(10.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {

                Text(
                    text = task.title,
                    color = if (task.isCompleted) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                    fontSize = 14.sp,
                    maxLines = 2,
                    textDecoration = if (task.isCompleted) {
                        androidx.compose.ui.text.style.TextDecoration.LineThrough
                    } else {
                        androidx.compose.ui.text.style.TextDecoration.None
                    }
                )

                Text(
                    text = "%02d:%02d – %02d:%02d".format(
                        task.startHour,
                        task.startMinute,
                        task.endHour,
                        task.endMinute
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )

                if (task.isCompleted) {
                    Text(
                        text = "انجام شد",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            IconButton(
                onClick = onToggle,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = if (task.isCompleted) {
                        Icons.Default.CheckCircle
                    } else {
                        Icons.Outlined.RadioButtonUnchecked
                    },
                    contentDescription = if (task.isCompleted) {
                        "علامت‌گذاری به‌عنوان انجام‌نشده"
                    } else {
                        "علامت‌گذاری به‌عنوان انجام‌شده"
                    },
                    tint = if (task.isCompleted) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "حذف کار",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(19.dp)
                )
            }
        }
    }
}