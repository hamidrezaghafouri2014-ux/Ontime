package com.androlearn.ot.ui.recovery

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.androlearn.ot.domain.recovery.RecoveryResult
import com.androlearn.ot.domain.recovery.RecoverySuggestion
import com.androlearn.ot.domain.recovery.UnscheduledTask

@Composable
fun SmartRecoveryScreen(
    date: String,
    day: String,
    nowMinute: Int,
    nowMillis: Long,
    modifier: Modifier = Modifier,
    viewModel: SmartRecoveryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "بازیابی هوشمند برنامه",
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            text = "اگر از برنامه عقب افتادی، زمان‌بندی پیشنهادی جدیدت را بساز.",
            style = MaterialTheme.typography.bodyMedium
        )

        Button(
            onClick = {
                viewModel.generatePlan(
                    date = date,
                    day = day,
                    nowMinute = nowMinute,
                    nowMillis = nowMillis
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("ساخت برنامه پیشنهادی")
        }

        when (val state = uiState) {
            RecoveryUiState.Idle -> {
                Text("برای شروع، دکمه بالا را بزن.")
            }

            RecoveryUiState.Loading -> {
                CircularProgressIndicator()
                Text("در حال بررسی برنامه...")
            }

            is RecoveryUiState.Error -> {
                Text(
                    text = state.message,
                    color = MaterialTheme.colorScheme.error
                )
            }

            is RecoveryUiState.Success -> {
                RecoveryResultContent(state.result)
            }
        }
    }
}

@Composable
private fun RecoveryResultContent(
    result: RecoveryResult
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    "زمان آزاد",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    "${result.availableMinutes} دقیقه",
                    style = MaterialTheme.typography.headlineMedium
                )

                Text(
                    "این مقدار، زمان آزاد پیش از اعمال پیشنهادهاست.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Text(
            "پیشنهادهای جدید",
            style = MaterialTheme.typography.titleLarge
        )

        if (result.suggestions.isEmpty()) {
            Text("پیشنهادی برای زمان‌بندی مجدد پیدا نشد.")
        } else {
            result.suggestions.forEach { suggestion ->
                SuggestionCard(suggestion)
            }
        }

        if (result.unscheduledTasks.isNotEmpty()) {
            HorizontalDivider()

            Text(
                "کارهای زمان‌بندی‌نشده",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.error
            )

            result.unscheduledTasks.forEach { task ->
                UnscheduledTaskCard(task)
            }
        }
    }
}

@Composable
private fun SuggestionCard(
    suggestion: RecoverySuggestion
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                suggestion.taskTitle,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                "زمان قبلی: ${
                    formatMinute(suggestion.originalStartMinute)
                } تا ${
                    formatMinute(suggestion.originalEndMinute)
                }"
            )

            Text(
                "زمان پیشنهادی: ${
                    formatMinute(suggestion.suggestedStartMinute)
                } تا ${
                    formatMinute(suggestion.suggestedEndMinute)
                }",
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                suggestion.reason,
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                "امتیاز اولویت: ${suggestion.priorityScore}",
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
private fun UnscheduledTaskCard(
    task: UnscheduledTask
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                task.taskTitle,
                style = MaterialTheme.typography.titleMedium
            )

            Text(task.reason)
        }
    }
}

private fun formatMinute(totalMinutes: Int): String {
    val hour = totalMinutes / 60
    val minute = totalMinutes % 60

    return "%02d:%02d".format(hour, minute)
}