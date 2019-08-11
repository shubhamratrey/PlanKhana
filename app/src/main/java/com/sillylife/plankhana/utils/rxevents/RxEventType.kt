package com.sillylife.plankhana.utils.rxevents

enum class RxEventType {
    SUBSCRIBE,
    EPISODE_DOWNLOAD,
    ACCOUNT_PROFILE_CLICKED;

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