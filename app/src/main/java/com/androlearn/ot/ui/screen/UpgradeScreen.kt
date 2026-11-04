package com.androlearn.ot.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun UpgradeScreen(
    navController: NavController
) {
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
            IconButton(onClick = { navController.popBackStack() }) {
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
            text = "با OnTime Pro بیشتر پیش برو",
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

        Button(
            onClick = {
                // اتصال پرداخت مایکت در مرحله بعد
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("خرید نسخه Pro")
        }

        Text(
            text = "پرداخت هنوز فعال نشده است.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}