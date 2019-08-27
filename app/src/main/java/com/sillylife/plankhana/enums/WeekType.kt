package com.sillylife.plankhana.enums

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

enum class WeekType constructor(val day: String, val date: Date) {
    TODAY(DateUtil.todayDay(), DateUtil.todayDate()),
    YESTERDAY(DateUtil.yesterday(), DateUtil.yesterdayDate()),
    TOMORROW(DateUtil.tomorrow(), DateUtil.tomorrowDate());

    companion object {
        fun getByDay(day: String): WeekType {
            for (le in values()) {
                if (le.day.equals(day, ignoreCase = true)) {
                    return le
                }
            }
            return TODAY
        }

        fun getByDate(date: Date): WeekType {
            for (le in values()) {
                if (le.date == date) {
                    return le
                }
            }
            return TODAY
        }
    }

    @SuppressLint("DefaultLocale")
    object DateUtil {

        fun todayDay(): String {
            val sdf = SimpleDateFormat("EEEE", Locale.US)
            val d = Date()
            val dayOfTheWeek = sdf.format(d)

            return dayOfTheWeek.toLowerCase()
        }

        fun todayDate(): Date {
            return Date()
        }

        fun yesterday(): String {
            val sdf = SimpleDateFormat("EEEE", Locale.US)
            val calendar = Calendar.getInstance()
            calendar.time = Date()
            calendar.add(Calendar.DATE, -1)
            val yesterday = sdf.format(calendar.time)
            return yesterday.toLowerCase()
        }

        fun yesterdayDate(): Date {
            val calendar = Calendar.getInstance()
            calendar.time = Date()
            calendar.add(Calendar.DATE, -1)
            return calendar.time
        }

        fun tomorrow(): String {
            val sdf = SimpleDateFormat("EEEE", Locale.US)
            val calendar = Calendar.getInstance()
            calendar.time = Date()
            calendar.add(Calendar.DATE, +1)
            val tomorrow = sdf.format(calendar.time)
            return tomorrow.toLowerCase()
        }

        fun tomorrowDate(): Date {
            val calendar = Calendar.getInstance()
            calendar.time = Date()
            calendar.add(Calendar.DATE, +1)
            return calendar.time
        }
    }
}