package com.sillylife.plankhana.fcm

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson

class FirebaseMessaging : FirebaseMessagingService() {

    private val TAG = FirebaseMessaging::class.java.simpleName
    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated.

        if (remoteMessage!!.data != null && remoteMessage.data.isEmpty()) {
            Log.d(TAG, "Null value ")
            return
        }
        Log.d(TAG, Gson().toJson(remoteMessage.data))
        val data = remoteMessage.data
        if (data != null) {

        }
    }

    override fun onNewToken(s: String?) {
        super.onNewToken(s)
    }
}
