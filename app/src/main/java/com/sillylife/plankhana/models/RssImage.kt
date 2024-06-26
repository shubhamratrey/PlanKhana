package com.sillylife.plankhana.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RssImage(
        @SerializedName("original_image") var original: String?,
        @SerializedName("thumbnail_url") var thumbnail: String?) : Parcelable
