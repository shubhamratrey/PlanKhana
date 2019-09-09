package com.sillylife.plankhana.models

import com.google.gson.annotations.SerializedName

data class Message(var to: String,
                   @SerializedName("time_to_live") var ttl: Int = 86400,
                   var data: NotifyData
)
