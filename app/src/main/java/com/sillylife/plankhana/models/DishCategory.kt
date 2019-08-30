package com.sillylife.plankhana.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DishCategory(
    var id: Int? = null,
    var category: String? = null
) : Parcelable