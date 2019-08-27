package com.sillylife.plankhana.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(@SerializedName("id") var id: Int? = null,
                @SerializedName("username") var name: String? = null,
                @SerializedName("display_picture") var imageUrl: String? = null,
                @SerializedName("phone") var phone: String? = null
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

    constructor(id: Int?, name: String?, imageUrl: String?) : this(null, null, null, null) {
        this.id = id
        this.name = name
        this.imageUrl = imageUrl
    }

}