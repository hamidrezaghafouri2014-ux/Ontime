package com.androlearn.ot.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.material3.MaterialTheme
import kotlinx.coroutines.delay
import java.util.Calendar
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun VintageAlarmClockBackground(
    modifier: Modifier = Modifier
) {

    /*
     * زمان واقعی سیستم
     */
    var currentTimeMillis by remember {
        mutableLongStateOf(System.currentTimeMillis())
    }

    /*
     * هر 50 میلی‌ثانیه آپدیت می‌شود
     * تا حرکت عقربه ثانیه نرم‌تر باشد.
     */
    LaunchedEffect(Unit) {
        while (true) {
            currentTimeMillis = System.currentTimeMillis()
            delay(50L)
        }
    }

    /*
     * Theme را اینجا می‌گیریم،
     * چون MaterialTheme یک Composable است.
     */
    val colorScheme = MaterialTheme.colorScheme

    val primary = colorScheme.primary
    val secondary = colorScheme.secondary
    val surface = colorScheme.surface

    /*
     * زمان
     */
    val calendar = remember(currentTimeMillis) {
        Calendar.getInstance().apply {
            timeInMillis = currentTimeMillis
        }
    }

    val hour = calendar.get(Calendar.HOUR)
    val minute = calendar.get(Calendar.MINUTE)
    val second = calendar.get(Calendar.SECOND)
    val millisecond = calendar.get(Calendar.MILLISECOND)

    /*
     * موقعیت ثانیه با میلی‌ثانیه
     */
    val secondProgress =
        second + millisecond / 1000f

    /*
     * زاویه عقربه‌ها
     */
    val secondAngle =
        secondProgress * 6f - 90f

    val minuteAngle =
        (minute + secondProgress / 60f) * 6f - 90f

    val hourAngle =
        ((hour % 12) + minute / 60f) * 30f - 90f

    /*
     * انیمیشن چکش
     *
     * حرکت آرام رفت و برگشتی.
     */
    val hammerAnimation =
        sin((currentTimeMillis % 3000L) / 3000f * Math.PI * 2)
            .toFloat()

    Box(
        modifier = modifier.fillMaxSize()
    ) {

        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {

            /*
             * ─────────────────────────────
             * اندازه و موقعیت ساعت
             * ─────────────────────────────
             */

            val clockRadius =
                minOf(size.width, size.height) * 0.27f

            val center = Offset(
                x = size.width * 0.50f,
                y = size.height * 0.48f
            )

            /*
             * اندازه زنگوله‌ها
             */
            val bellRadius = clockRadius * 0.30f

            /*
             * ─────────────────────────────
             * سایه بسیار ظریف
             * ─────────────────────────────
             */

            drawCircle(
                color = Color.Black.copy(alpha = 0.08f),
                radius = clockRadius * 1.04f,
                center = Offset(
                    center.x,
                    center.y + clockRadius * 0.04f
                )
            )

            /*
             * ─────────────────────────────
             * پایه‌های ساعت
             * ─────────────────────────────
             */

            val legWidth = clockRadius * 0.12f
            val legHeight = clockRadius * 0.28f

            drawLine(
                color = primary.copy(alpha = 0.20f),
                start = Offset(
                    center.x - clockRadius * 0.55f,
                    center.y + clockRadius * 0.75f
                ),
                end = Offset(
                    center.x - clockRadius * 0.72f,
                    center.y + clockRadius * 0.75f + legHeight
                ),
                strokeWidth = legWidth,
                cap = StrokeCap.Round
            )

            drawLine(
                color = primary.copy(alpha = 0.20f),
                start = Offset(
                    center.x + clockRadius * 0.55f,
                    center.y + clockRadius * 0.75f
                ),
                end = Offset(
                    center.x + clockRadius * 0.72f,
                    center.y + clockRadius * 0.75f + legHeight
                ),
                strokeWidth = legWidth,
                cap = StrokeCap.Round
            )

            /*
             * ─────────────────────────────
             * پایه‌های زنگوله
             * ─────────────────────────────
             */

            val bellStandY =
                center.y - clockRadius * 0.98f

            drawLine(
                color = primary.copy(alpha = 0.22f),
                start = Offset(
                    center.x - clockRadius * 0.42f,
                    center.y - clockRadius * 0.82f
                ),
                end = Offset(
                    center.x - clockRadius * 0.48f,
                    bellStandY
                ),
                strokeWidth = clockRadius * 0.08f,
                cap = StrokeCap.Round
            )

            drawLine(
                color = primary.copy(alpha = 0.22f),
                start = Offset(
                    center.x + clockRadius * 0.42f,
                    center.y - clockRadius * 0.82f
                ),
                end = Offset(
                    center.x + clockRadius * 0.48f,
                    bellStandY
                ),
                strokeWidth = clockRadius * 0.08f,
                cap = StrokeCap.Round
            )

            /*
             * ─────────────────────────────
             * زنگوله سمت چپ
             * ─────────────────────────────
             */

            val leftBellCenter = Offset(
                center.x - clockRadius * 0.48f,
                center.y - clockRadius * 1.18f
            )

            drawCircle(
                color = surface.copy(alpha = 0.20f),
                radius = bellRadius,
                center = leftBellCenter
            )

            drawCircle(
                color = primary.copy(alpha = 0.20f),
                radius = bellRadius,
                center = leftBellCenter,
                style = Stroke(
                    width = clockRadius * 0.035f
                )
            )

            /*
             * لبه زنگوله چپ
             */
            drawArc(
                color = primary.copy(alpha = 0.22f),
                startAngle = 0f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(
                    leftBellCenter.x - bellRadius,
                    leftBellCenter.y - bellRadius * 0.20f
                ),
                size = androidx.compose.ui.geometry.Size(
                    bellRadius * 2f,
                    bellRadius * 1.1f
                ),
                style = Stroke(
                    width = clockRadius * 0.045f
                )
            )

            /*
             * ─────────────────────────────
             * زنگوله سمت راست
             * ─────────────────────────────
             */

            val rightBellCenter = Offset(
                center.x + clockRadius * 0.48f,
                center.y - clockRadius * 1.18f
            )

            drawCircle(
                color = surface.copy(alpha = 0.20f),
                radius = bellRadius,
                center = rightBellCenter
            )

            drawCircle(
                color = primary.copy(alpha = 0.20f),
                radius = bellRadius,
                center = rightBellCenter,
                style = Stroke(
                    width = clockRadius * 0.035f
                )
            )

            /*
             * لبه زنگوله راست
             */
            drawArc(
                color = primary.copy(alpha = 0.22f),
                startAngle = 0f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(
                    rightBellCenter.x - bellRadius,
                    rightBellCenter.y - bellRadius * 0.20f
                ),
                size = androidx.compose.ui.geometry.Size(
                    bellRadius * 2f,
                    bellRadius * 1.1f
                ),
                style = Stroke(
                    width = clockRadius * 0.045f
                )
            )

            /*
             * ─────────────────────────────
             * چکش وسط
             * ─────────────────────────────
             */

            val hammerAngle =
                hammerAnimation * 10f

            val hammerLength =
                clockRadius * 0.40f

            val hammerX =
                center.x +
                        sin(Math.toRadians(hammerAngle.toDouble()))
                            .toFloat() *
                        hammerLength

            val hammerY =
                center.y -
                        clockRadius * 1.16f

            /*
             * میله چکش
             */
            drawLine(
                color = secondary.copy(alpha = 0.28f),
                start = Offset(
                    center.x,
                    center.y - clockRadius * 0.92f
                ),
                end = Offset(
                    hammerX,
                    hammerY
                ),
                strokeWidth = clockRadius * 0.045f,
                cap = StrokeCap.Round
            )

            /*
             * سر چکش
             */
            drawCircle(
                color = secondary.copy(alpha = 0.25f),
                radius = clockRadius * 0.075f,
                center = Offset(
                    hammerX,
                    hammerY
                )
            )

            /*
             * ─────────────────────────────
             * بدنه اصلی ساعت
             * ─────────────────────────────
             */

            drawCircle(
                color = surface.copy(alpha = 0.30f),
                radius = clockRadius,
                center = center
            )

            /*
             * قاب بیرونی
             */
            drawCircle(
                color = primary.copy(alpha = 0.22f),
                radius = clockRadius,
                center = center,
                style = Stroke(
                    width = clockRadius * 0.055f
                )
            )

            /*
             * قاب داخلی
             */
            drawCircle(
                color = secondary.copy(alpha = 0.10f),
                radius = clockRadius * 0.88f,
                center = center,
                style = Stroke(
                    width = clockRadius * 0.025f
                )
            )

            /*
             * ─────────────────────────────
             * Tick های ساعت
             * ─────────────────────────────
             */

            for (i in 0 until 60) {

                val angle =
                    Math.toRadians(
                        i * 6.0 - 90.0
                    )

                val isHour =
                    i % 5 == 0

                val outer =
                    clockRadius * 0.80f

                val inner =
                    if (isHour) {
                        clockRadius * 0.69f
                    } else {
                        clockRadius * 0.75f
                    }

                val start = Offset(
                    x = center.x +
                            cos(angle).toFloat() * inner,

                    y = center.y +
                            sin(angle).toFloat() * inner
                )

                val end = Offset(
                    x = center.x +
                            cos(angle).toFloat() * outer,

                    y = center.y +
                            sin(angle).toFloat() * outer
                )

                drawLine(
                    color = primary.copy(
                        alpha = if (isHour) {
                            0.16f
                        } else {
                            0.045f
                        }
                    ),
                    start = start,
                    end = end,
                    strokeWidth =
                        if (isHour) {
                            clockRadius * 0.025f
                        } else {
                            clockRadius * 0.010f
                        },
                    cap = StrokeCap.Round
                )
            }

            /*
             * ─────────────────────────────
             * عقربه ساعت
             * ─────────────────────────────
             */

            val hourRad =
                Math.toRadians(hourAngle.toDouble())

            drawLine(
                color = secondary.copy(alpha = 0.24f),
                start = center,
                end = Offset(
                    center.x +
                            cos(hourRad).toFloat() *
                            clockRadius * 0.47f,

                    center.y +
                            sin(hourRad).toFloat() *
                            clockRadius * 0.47f
                ),
                strokeWidth = clockRadius * 0.045f,
                cap = StrokeCap.Round
            )

            /*
             * ─────────────────────────────
             * عقربه دقیقه
             * ─────────────────────────────
             */

            val minuteRad =
                Math.toRadians(minuteAngle.toDouble())

            drawLine(
                color = primary.copy(alpha = 0.25f),
                start = center,
                end = Offset(
                    center.x +
                            cos(minuteRad).toFloat() *
                            clockRadius * 0.67f,

                    center.y +
                            sin(minuteRad).toFloat() *
                            clockRadius * 0.67f
                ),
                strokeWidth = clockRadius * 0.032f,
                cap = StrokeCap.Round
            )

            /*
             * ─────────────────────────────
             * عقربه ثانیه
             * ─────────────────────────────
             */

            val secondRad =
                Math.toRadians(secondAngle.toDouble())

            drawLine(
                color = primary.copy(alpha = 0.32f),
                start = Offset(
                    center.x -
                            cos(secondRad).toFloat() *
                            clockRadius * 0.12f,

                    center.y -
                            sin(secondRad).toFloat() *
                            clockRadius * 0.12f
                ),
                end = Offset(
                    center.x +
                            cos(secondRad).toFloat() *
                            clockRadius * 0.76f,

                    center.y +
                            sin(secondRad).toFloat() *
                            clockRadius * 0.76f
                ),
                strokeWidth = clockRadius * 0.012f,
                cap = StrokeCap.Round
            )

            /*
             * ─────────────────────────────
             * مرکز عقربه‌ها
             * ─────────────────────────────
             */

            drawCircle(
                color = primary.copy(alpha = 0.30f),
                radius = clockRadius * 0.055f,
                center = center
            )

            /*
             * ─────────────────────────────
             * پایه پایین ساعت
             * ─────────────────────────────
             */

            drawLine(
                color = primary.copy(alpha = 0.18f),
                start = Offset(
                    center.x - clockRadius * 0.45f,
                    center.y + clockRadius * 0.92f
                ),
                end = Offset(
                    center.x + clockRadius * 0.45f,
                    center.y + clockRadius * 0.92f
                ),
                strokeWidth = clockRadius * 0.08f,
                cap = StrokeCap.Round
            )
        }
    }
}