package com.sillylife.plankhana.enums

enum class ImageType constructor(val type: String) {
    USER_IMAGE("user_image");

    companion object {
        fun getByType(type: String): ImageType {
            for (le in values()) {
                if (le.type.equals(type, ignoreCase = true)) {
                    return le
                }
            }
            return USER_IMAGE
        }
    }

}