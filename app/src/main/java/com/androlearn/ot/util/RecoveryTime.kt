package com.androlearn.ot.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object RecoveryTime {

    private val days = listOf(
        "Sun", "Mon", "Tue", "Wed",
        "Thu", "Fri", "Sat"
    )

    fun currentDate(): String {
        return SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.US
        ).format(Date())
    }

    fun currentMinute(): Int {
        val calendar = Calendar.getInstance()

        return calendar.get(Calendar.HOUR_OF_DAY) * 60 +
                calendar.get(Calendar.MINUTE)
    }

    fun currentMillis(): Long = System.currentTimeMillis()

    // این فرمت فقط وقتی درست است که تاریخ تکمیل‌ها هم همین فرمت را داشته باشد.
    fun currentDate(pattern: String): String {
        return SimpleDateFormat(pattern, Locale.US).format(Date())
    }
}