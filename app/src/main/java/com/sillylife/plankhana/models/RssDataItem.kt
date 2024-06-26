package com.sillylife.plankhana.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RssDataItem(
        var title: String?,
        var description: String?,
        var link: String?,
        val images: RssImage?,
        @SerializedName("published_date") var dateTime: String?,
        var source: String?) : Parcelable
