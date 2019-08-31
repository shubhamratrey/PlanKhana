package com.sillylife.plankhana.utils.rxevents

enum class RxEventType {
    DISH_ADDED_REMOVED;

    companion object {

        fun get(name: String): RxEventType? {
            for (le in values()) {
                if (le.name.equals(name, ignoreCase = true)) {
                    return le
                }
            }
            return null
        }
    }

}