package com.androlearn.ot.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.androlearn.ot.ui.theme.TaskBlue
import com.androlearn.ot.ui.theme.TaskGreen
import com.androlearn.ot.ui.theme.TaskOrange
import com.androlearn.ot.ui.theme.TaskPurple
import com.androlearn.ot.ui.theme.TaskRed
import com.androlearn.ot.ui.theme.TaskTeal
import com.androlearn.ot.viewmodel.AddTaskViewModel
import com.androlearn.ot.util.JalaliCalendar
import java.util.Calendar
import android.app.DatePickerDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.ui.text.font.FontWeight
import com.androlearn.ot.ui.theme.faFont
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTask(
    navController: NavHostController,
    paddingValues: PaddingValues,
    onBack: () -> Unit = {
        navController.popBackStack()
    },
    viewModel: AddTaskViewModel = hiltViewModel()
) {

    val formatter = DateTimeFormatter.ofPattern("HH:mm")

    val start = runCatching {
        LocalTime.parse(viewModel.startTime, formatter)
    }.getOrNull()

    val end = runCatching {
        LocalTime.parse(viewModel.endTime, formatter)
    }.getOrNull()

    val days = listOf(
        "Sat" to "شنبه",
        "Sun" to "یکشنبه",
        "Mon" to "دوشنبه",
        "Tue" to "سه‌شنبه",
        "Wed" to "چهارشنبه",
        "Thu" to "پنجشنبه",
        "Fri" to "جمعه"
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = paddingValues.calculateTopPadding() + 12.dp,
                        start = 20.dp,
                        end = 20.dp,
                        bottom = 16.dp
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "بازگشت",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "کار جدید",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontFamily = faFont
                    )

                    Text(
                        text = "قدم بعدی‌ات را مشخص کن",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontFamily = faFont
                    )
                }
            }
        }
    ) {
            paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = "Task Title",
                style = MaterialTheme.typography.titleMedium
            )

            OnTimeTextField(
                value = viewModel.title,
                onValueChange = { viewModel.title = it },
                label = "عنوان کار",
                placeholder = "مثلاً: مطالعه‌ی Kotlin"
            )

            Text(
                text = "Days",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = if (viewModel.selectedDays.isEmpty()) {
                    "حداقل یک روز انتخاب کن"
                } else {
                    "${viewModel.selectedDays.size} روز انتخاب شده"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                days.forEach { (english, persian) ->

                    val selected =
                        viewModel.selectedDays.contains(english)

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.toggleDay(english)
                            },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor =
                                if (selected) {
                                    MaterialTheme.colorScheme.primary
                                        .copy(alpha = 0.15f)
                                } else {
                                    MaterialTheme.colorScheme.surface
                                }
                        ),
                        border = BorderStroke(
                            width = 1.dp,
                            color =
                                if (selected) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.outline
                                }
                        )
                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = 16.dp,
                                    vertical = 12.dp
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(
                                        color =
                                            if (selected) {
                                                MaterialTheme.colorScheme.primary
                                            } else {
                                                Color.Transparent
                                            },
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {

                                if (selected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Spacer(
                                modifier = Modifier.width(12.dp)
                            )

                            Column {

                                Text(
                                    text = persian,
                                    style = MaterialTheme.typography.bodyLarge
                                )

                                Text(
                                    text = english,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            val context = LocalContext.current

            Text(
                text = "تاریخ پایان تسک (تسک تا کی ادامه پیدا کند؟)",
                style = MaterialTheme.typography.titleMedium
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val cal = Calendar.getInstance()
                        DatePickerDialog(
                            context,
                            { _, year, month, dayOfMonth ->
                                val pickedCal = Calendar.getInstance().apply {
                                    set(year, month, dayOfMonth, 23, 59, 59)
                                    set(Calendar.MILLISECOND, 0)
                                }
                                viewModel.endDateMillis = pickedCal.timeInMillis
                                val jalali = JalaliCalendar.fromGregorian(pickedCal)
                                viewModel.endDateText = "${jalali.year}/${jalali.month}/${jalali.day} (${jalali.monthName})"
                            },
                            cal.get(Calendar.YEAR),
                            cal.get(Calendar.MONTH),
                            cal.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline
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
                            text = if (viewModel.endDateText.isEmpty()) "بدون تاریخ پایان (تا همیشه)" else viewModel.endDateText,
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (viewModel.endDateText.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "برای انتخاب تاریخ پایان از تقویم کلیک کنید",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    if (viewModel.endDateText.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                viewModel.endDateMillis = 0L
                                viewModel.endDateText = ""
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "حذف تاریخ پایان",
                                tint = Color.Red
                            )
                        }
                    }
                }
            }

            Text(
                text = "Reminder",
                style = MaterialTheme.typography.titleMedium
            )

            OutlinedTextField(
                value = viewModel.reminderMinutes,
                onValueChange = { value ->

                    viewModel.reminderMinutes = value
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                label = {
                    Text("Minutes before task")
                },
                supportingText = {
                    Text(
                        "مثلاً 10 یعنی ۱۰ دقیقه قبل از شروع کار (پیامک / یادآوری)"
                    )
                }
            )

            // Task Color
            Text(
                text = "Task Color",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(12.dp))
            val taskColors = listOf(
                TaskBlue,
                TaskGreen,
                TaskRed,
                TaskOrange,
                TaskPurple,
                TaskTeal
            )
            Row(
                modifier =
                    Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                taskColors.forEach { color ->
                    val isSelected = viewModel.color == color.toArgb()
                    Box (
                        modifier =
                            Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    width = if (isSelected) 3.dp else 0.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { viewModel.color = color.toArgb() })
                }
            }


            Text(
                text = "Time",
                style = MaterialTheme.typography.titleMedium
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                OutlinedTextField(
                    value = viewModel.startTime,
                    onValueChange = {
                        viewModel.startTime = it
                    },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    label = {
                        Text("شروع ساعت کار که مثلا 12:00")
                    }
                )

                OutlinedTextField(
                    value = viewModel.endTime,
                    onValueChange = {
                        viewModel.endTime = it
                    },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    label = {
                        Text("پایان ساعت کار که مثلا 12:00")
                    }
                )
            }

            viewModel.timeError?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Text(
                text = "Notification",
                style = MaterialTheme.typography.titleMedium
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {

                        Text(
                            text = "Enable notification",
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Text(
                            text = "قبل از شروع کار بهت یادآوری می‌کنیم",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 10.dp)
                        )
                    }

                    Switch(
                        checked = viewModel.notificationEnabled,
                        onCheckedChange = {
                            viewModel.notificationEnabled = it
                        }
                    )
                }
            }

            if (viewModel.notificationEnabled) {

                OutlinedTextField(
                    value = viewModel.question,
                    onValueChange = {
                        viewModel.question = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = {
                        Text("Notification question")
                    }
                )
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Button(
                onClick = {
                    viewModel.saveTask {
                        onBack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null
                )

                Spacer(Modifier.width(10.dp))

                Text(
                    text = "ذخیره‌ی کار",
                    fontFamily = faFont,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )
        }
    }
}

@Composable
private fun OnTimeTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = ""
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = {
            Text(
                text = label,
                fontFamily = faFont
            )
        },
        placeholder = {
            Text(
                text = placeholder,
                fontFamily = faFont
            )
        },
        shape = RoundedCornerShape(18.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface
        )
    )
}