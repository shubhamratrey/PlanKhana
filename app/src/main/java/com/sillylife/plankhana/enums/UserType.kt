package com.sillylife.plankhana.enums

enum class UserType constructor(val type: String) {
    RESIDENT("resident"),
    COOK("cook");

    companion object {
        fun getByType(type: String): UserType {
            for (le in values()) {
                if (le.type.equals(type, ignoreCase = true)) {
                    return le
                }
            }
            return RESIDENT
        }
    }

}