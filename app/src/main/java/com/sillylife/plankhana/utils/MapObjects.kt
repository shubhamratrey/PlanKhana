package com.sillylife.plankhana.utils

import com.sillylife.plankhana.enums.UserType
import com.sillylife.plankhana.type.Plankhana_houses_houseuser_insert_input
import com.sillylife.plankhana.type.Plankhana_users_userprofile_insert_input
import com.sillylife.plankhana.type.Plankhana_users_userprofile_obj_rel_insert_input


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
}
