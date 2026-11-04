package com.androlearn.ot.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import java.util.Calendar

@Composable
fun AnalogClock(
    modifier: Modifier = Modifier,
    color: Color = Color(0xFF00C896)
) {
    val now = Calendar.getInstance()

    val hour = now.get(Calendar.HOUR)
    val minute = now.get(Calendar.MINUTE)
    val second = now.get(Calendar.SECOND)

    val hourAngle = hour * 30f + minute * 0.5f - 90f
    val minuteAngle = minute * 6f + second * 0.1f - 90f
    val secondAngle = second * 6f - 90f

    Canvas(modifier = modifier.size(180.dp)) {
        val center = this.center
        val radius = size.minDimension / 2f

        drawCircle(
            color = color.copy(alpha = 0.15f),
            radius = radius,
            center = center
        )

        drawCircle(
            color = color,
            radius = radius,
            center = center,
            style = Stroke(width = 3.dp.toPx())
        )

        fun drawHand(angle: Float, length: Float, width: Float, handColor: Color) {
            val radians = Math.toRadians(angle.toDouble())
            val endX = center.x + kotlin.math.cos(radians).toFloat() * radius * length
            val endY = center.y + kotlin.math.sin(radians).toFloat() * radius * length

            drawLine(
                color = handColor,
                start = center,
                end = androidx.compose.ui.geometry.Offset(endX, endY),
                strokeWidth = width.dp.toPx(),
                cap = StrokeCap.Round
            )
        }

        drawHand(hourAngle, 0.48f, 5f, color)
        drawHand(minuteAngle, 0.68f, 3f, color)
        drawHand(secondAngle, 0.78f, 1.5f, Color(0xFFFF527A))

        drawCircle(color = color, radius = 5.dp.toPx(), center = center)
    }
}