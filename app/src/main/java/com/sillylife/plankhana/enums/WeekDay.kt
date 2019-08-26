package com.sillylife.plankhana.enums

enum class WeekDay constructor(val day: String) {
    MONDAY("monday"),
    TUESDAY("tuesday"),
    WEDNESDAY("wednesday"),
    THURSDAY("thursday"),
    FRIDAY("friday"),
    SATURDAY("saturday"),
    SUNDAY("sunday");

    companion object {
        fun getByType(type: String): WeekDay {
            for (le in values()) {
                if (le.day.equals(type, ignoreCase = true)) {
                    return le
                }
            }
            return MONDAY
        }
    }
}