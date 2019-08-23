package com.sillylife.plankhana.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Food(@SerializedName("id") var id: Int? = null,
                @SerializedName("name") var foodName: String? = null,
                @SerializedName("image_url") var foodImage: String? = null,
                @SerializedName("is_added") var isAdded: Boolean? = null
) : Parcelable