package com.sillylife.plankhana.utils

import com.sillylife.plankhana.enums.WeekDay
import java.text.SimpleDateFormat
import java.util.*


object DateUtil {

    fun today(): WeekDay {
        val sdf = SimpleDateFormat("EEEE", Locale.US)
        val d = Date()
        val dayOfTheWeek = sdf.format(d)
        return WeekDay.getByType(dayOfTheWeek.toLowerCase())
    }

    fun yesterday(): WeekDay {
        val sdf = SimpleDateFormat("EEEE", Locale.US)
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        calendar.add(Calendar.DATE, -1)
        val yesterday = sdf.format(calendar.time)


        return WeekDay.getByType(yesterday.toLowerCase())
    }

    fun tomorrow(): WeekDay {
        val sdf = SimpleDateFormat("EEEE", Locale.US)
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        calendar.add(Calendar.DATE, +1)
        val tomorrow = sdf.format(calendar.time)
        return WeekDay.getByType(tomorrow.toLowerCase())
    }
}