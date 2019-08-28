package com.sillylife.plankhana.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DishStatus(var added: Boolean = false,
                      var add: Boolean = false
) : Parcelable