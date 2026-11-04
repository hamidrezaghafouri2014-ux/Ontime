package com.androlearn.ot.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.max
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorPickerSheet(
    initialColor: Color,
    onDismiss: () -> Unit,
    onColorSelected: (Color) -> Unit
) {
    val initialHsv = remember(initialColor) {
        FloatArray(3).also {
            android.graphics.Color.colorToHSV(
                initialColor.toArgb(),
                it
            )
        }
    }

    var hue by remember { mutableFloatStateOf(initialHsv[0]) }
    var saturation by remember { mutableFloatStateOf(initialHsv[1]) }
    var value by remember { mutableFloatStateOf(initialHsv[2]) }

    val selectedColor = remember(hue, saturation, value) {
        Color.hsv(
            hue,
            saturation,
            value
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    bottom = 28.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "انتخاب رنگ",
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            ColorSpectrum(
                hue = hue,
                saturation = saturation,
                value = value,
                onChanged = { s, v ->
                    saturation = s
                    value = v
                }
            )

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            HueSlider(
                hue = hue,
                onHueChanged = {
                    hue = it
                }
            )

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(selectedColor)
                )

                Text(
                    text = selectedColor.toHex(),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            Button(
                onClick = {
                    onColorSelected(selectedColor)
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "انتخاب این رنگ"
                )
            }
        }
    }
}

@Composable
private fun ColorSpectrum(
    hue: Float,
    saturation: Float,
    value: Float,
    onChanged: (Float, Float) -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(18.dp))
            .pointerInput(hue) {
                detectDragGestures(
                    onDragStart = { offset ->
                        updateSpectrum(
                            offset,
                            size.width,
                            size.height,
                            onChanged
                        )
                    },
                    onDrag = { change, _ ->
                        updateSpectrum(
                            change.position,
                            size.width,
                            size.height,
                            onChanged
                        )
                    }
                )
            }
    ) {

        Canvas(
            modifier = Modifier.matchParentSize()
        ) {

            val hueColor = Color.hsv(
                hue,
                1f,
                1f
            )

            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.White,
                        hueColor
                    )
                )
            )

            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.Black
                    )
                )
            )

            val x = saturation * size.width
            val y = (1f - value) * size.height

            drawCircle(
                color = Color.White,
                radius = 10.dp.toPx(),
                center = Offset(x, y)
            )

            drawCircle(
                color = Color.Black,
                radius = 7.dp.toPx(),
                center = Offset(x, y)
            )
        }
    }
}

private fun updateSpectrum(
    offset: Offset,
    width: Int,
    height: Int,
    onChanged: (Float, Float) -> Unit
) {

    val saturation = min(
        1f,
        max(
            0f,
            offset.x / width
        )
    )

    val value = 1f - min(
        1f,
        max(
            0f,
            offset.y / height
        )
    )

    onChanged(
        saturation,
        value
    )
}

@Composable
private fun HueSlider(
    hue: Float,
    onHueChanged: (Float) -> Unit
) {

    Slider(
        value = hue,
        onValueChange = onHueChanged,
        valueRange = 0f..360f,
        modifier = Modifier.fillMaxWidth()
    )
}

private fun Color.toHex(): String {
    return String.format(
        "#%02X%02X%02X",
        (red * 255).toInt(),
        (green * 255).toInt(),
        (blue * 255).toInt()
    )
}