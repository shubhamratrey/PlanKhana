package com.sillylife.plankhana.utils

import com.sillylife.plankhana.enums.UserType
import com.sillylife.plankhana.type.*


object MapObjects {

    fun addResident(houseId: Int, userName: String, userPicture: String, userNumber: String, userType: UserType): Plankhana_houses_houseuser_insert_input {
        val data = Plankhana_users_userprofile_insert_input.builder()
                .display_picture(userPicture)
                .username(userName)
                .user_type(userType.type)
                .phone(userNumber)
                .build()

        val usersUserProfile = Plankhana_users_userprofile_obj_rel_insert_input.builder()
                .data(data)
                .build()

        return Plankhana_houses_houseuser_insert_input.builder()
                .users_userprofile(usersUserProfile)
                .house_id(houseId)
                .build()
    }

    fun addDishes(houseId: Int, dishId: Int, userId: Int, weekdayId: Int): Plankhana_users_userdishweekplan_insert_input {
        return Plankhana_users_userdishweekplan_insert_input.builder()
                .user_id(userId)
                .house_id(houseId)
                .dish_id(dishId)
                .weekday_id(weekdayId)
                .build()
    }

}
