package com.sillylife.plankhana.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Dish(
        var id: Int? = null,
        var dishName: String? = null,
        var dishImage: String? = null,
        var userList: ArrayList<User>? = null,
        var dishStatus: DishStatus? = null
) : Parcelable {

    constructor(id: Int?, dishName: String? = null, dishImage: String?) : this(null, null, null, null) {
        this.id = id
        this.dishImage = dishImage
        this.dishName = dishName
    }

    constructor(id: Int?, dishName: String? = null, dishImage: String?, dishStatus: DishStatus) : this(null, null, null, null) {
        this.id = id
        this.dishImage = dishImage
        this.dishName = dishName
        this.dishStatus = dishStatus
    }

    constructor(id: Int?, dishName: String? = null, dishImage: String?, userList: ArrayList<User>?) : this(null, null, null, null, null) {
        this.id = id
        this.dishImage = dishImage
        this.dishName = dishName
        this.userList = userList
    }
}