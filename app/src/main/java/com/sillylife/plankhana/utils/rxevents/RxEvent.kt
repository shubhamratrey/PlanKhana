package com.sillylife.plankhana.utils.rxevents

import android.content.Intent

class RxEvent {
    data class UpdateSeekPosition(val channelId: Int, val episodeId: Int, val seekPosition: Int, val bufferPercentage: Int)
    data class ActivityResult(var requestCode: Int, var resultCode: Int, var data: Intent?)
    data class NetworkConnectivity(var isConnected: Boolean)
    data class UpdateLikeUnLike(var isLiked: Boolean, var channelId: Int, var episodeId: Int, var likeCount: Int?)
    class Action(var eventType: RxEventType, vararg val items: Any)
}