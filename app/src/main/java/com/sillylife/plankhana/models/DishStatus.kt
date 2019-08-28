package com.sillylife.plankhana.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DishStatus(var toAdd: Boolean = false,
                      var isAdded: Boolean = false,
                      var remove: Boolean = false
) : Parcelable