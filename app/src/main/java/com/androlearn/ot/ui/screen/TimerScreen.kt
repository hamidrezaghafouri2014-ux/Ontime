
package com.androlearn.ot.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.androlearn.ot.viewmodel.TaskViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TimerScreen(
    taskId: Int,
    navController: NavHostController,
    viewModel: TaskViewModel = hiltViewModel()
) {
    val task by viewModel.timerTask.collectAsState()

    var currentTime by remember {
        mutableLongStateOf(System.currentTimeMillis())
    }

    LaunchedEffect(taskId) {
        viewModel.loadOrStartTimer(taskId)
    }

    LaunchedEffect(task?.timerEndMillis) {
        while (task != null) {
            currentTime = System.currentTimeMillis()

            if (task!!.timerEndMillis <= currentTime) {
                break
            }

            delay(1000L)
        }

        currentTime = System.currentTimeMillis()
    }

    val timerEnd = task?.timerEndMillis ?: 0L

    val timeLeft = if (timerEnd > currentTime) {
        (timerEnd - currentTime) / 1000L
    } else {
        0L
    }

    val hours = timeLeft / 3600
    val minutes = (timeLeft % 3600) / 60
    val seconds = timeLeft % 60

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = task?.title ?: "در حال بارگذاری...",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.tertiary
        )

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = String.format(
                Locale.US,
                "%02d:%02d:%02d",
                hours,
                minutes,
                seconds
            ),
            fontSize = 64.sp,
            fontWeight = FontWeight.Light,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(64.dp))

        Button(
            onClick = {
                val currentTask = task ?: return@Button

                val date = SimpleDateFormat(
                    "yyyy-MM-dd",
                    Locale.US
                ).format(Date())

                viewModel.completeTimer(
                    taskId = currentTask.id,
                    date = date
                ) {
                    navController.popBackStack()
                }
            },
            enabled = task != null,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4CAF50)
            )
        ) {
            Icon(
                Icons.Default.Check,
                contentDescription = null
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                "انجام شد",
                fontSize = 18.sp
            )
        }
    }
}