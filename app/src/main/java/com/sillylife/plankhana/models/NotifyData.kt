package com.sillylife.plankhana.models

import com.google.gson.annotations.SerializedName

data class NotifyData(
        var title: String? = null,
        var description: String? = null,
        var image: String? = null,
        var uri: String? = null,
        @SerializedName("n_channel_id") var channelId: String = "Notification Home",
        @SerializedName("n_channel_name") var channelName: String = "PlanKhana Home",
        @SerializedName("n_channel_description") var channelDescription: String = "India's number one utility App",
        @SerializedName("n_channel_priority") var priority: Int = 5
) {
    constructor(title: String?, description: String?, image: String?) : this() {
        this.title = title
        this.description = description
        this.image = image
        this.channelId = "Notification Home"
        this.channelName = "PlanKhana Home"
        this.channelDescription = "India's number one utility App"
        this.priority = 5
    }
}