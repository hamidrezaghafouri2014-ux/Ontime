
package com.androlearn.ot.ui.screen

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.androlearn.ot.viewmodel.BillingViewModel

@Composable
fun UpgradeScreen(
    navController: NavController,
    viewModel: BillingViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }

    val isPro by viewModel.isPro.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(activity) {
        if (activity != null) {
            viewModel.connect(activity)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { navController.popBackStack() }
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "بازگشت")
            }

            Text(
                text = "OnTime Pro",
                style = MaterialTheme.typography.titleLarge
            )
        }

        Spacer(Modifier.height(20.dp))

        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(72.dp)
        )

        Text(
            text = if (isPro) "شما عضو OnTime Pro هستید"
            else "با OnTime Pro بیشتر پیش برو",
            style = MaterialTheme.typography.headlineSmall
        )

        Text("امکانات ویژه برای مدیریت بهتر زمان")

        val features = listOf(
            "اعمال پیشنهادهای Smart Recovery",
            "زمان‌بندی پیشرفته",
            "امکانات اختصاصی آینده"
        )

        features.forEach { feature ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = "✓  $feature",
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        Spacer(Modifier.weight(1f))

        if (!isPro) {
            Button(
                onClick = { viewModel.purchasePro() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                enabled = activity != null
            ) {
                Text("خرید نسخه Pro")
            }
        }

        error?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error
            )

            TextButton(onClick = { viewModel.clearError() }) {
                Text("متوجه شدم")
            }
        }

        Text(
            text = if (isPro) "نسخه Pro فعال است"
            else "پرداخت از طریق مایکت انجام می‌شود.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun Context.findActivity(): Activity? {
    var current = this

    while (current is ContextWrapper) {
        if (current is Activity) return current
        current = current.baseContext
    }

    return current as? Activity
}
