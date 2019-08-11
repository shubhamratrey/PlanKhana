package com.sillylife.plankhana.models

import android.net.Uri
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(@SerializedName("id") var id: Int? = null,
                @SerializedName("name") var name: String? = null,
                @SerializedName("image_url") var imageUrl: String? = null
) : Parcelable {

    fun hasName(): Boolean {
        return name != null && !name.isNullOrBlank()
    }

    constructor(id: Int?) : this(null, null, null) {
        this.id = id
    }

    constructor(id: Int?, imageUrl: String?) : this(null, null, null) {
        this.id = id
        this.imageUrl = imageUrl
    }

}