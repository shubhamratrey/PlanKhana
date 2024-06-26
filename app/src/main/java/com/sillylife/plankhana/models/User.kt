package com.sillylife.plankhana.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(@SerializedName("id") var id: Int? = null,
                @SerializedName("username") var name: String? = null,
                @SerializedName("display_picture") var imageUrl: String? = null,
                @SerializedName("phone") var phone: String? = null,
                @SerializedName("language_id") var languageId: Int? = null
) : Parcelable {

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

    constructor(id: Int?, languageId: Int?) : this() {
        this.id = id
        this.languageId = languageId
    }
}