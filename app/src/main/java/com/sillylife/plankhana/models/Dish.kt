package com.sillylife.plankhana.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Dish(
    var id: Int? = null,
    var dishName: String? = null,
    var dishImage: String? = null,
    var userList: ArrayList<User>? = null
) : Parcelable