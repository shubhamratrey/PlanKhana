package com.sillylife.plankhana.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.sillylife.plankhana.R
import com.sillylife.plankhana.constants.BundleConstants
import com.sillylife.plankhana.constants.IntentConstants
import com.sillylife.plankhana.constants.NotificationKeys
import com.sillylife.plankhana.utils.CommonUtil
import com.sillylife.plankhana.views.activities.AuntyActivity
import java.util.concurrent.atomic.AtomicInteger

class FirebaseMessaging : FirebaseMessagingService() {

    private val TAG = FirebaseMessaging::class.java.simpleName
    private var notificationManager: NotificationManager? = null
    private val NOTIFICATION_ID = AtomicInteger(0)

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
            val title = data[NotificationKeys.TITLE]
            val description = data[NotificationKeys.DESCRIPTION]
            val image = data[NotificationKeys.IMAGE]
            val uriString = data[NotificationKeys.URI]
            val notification_id: String? = data[NotificationKeys.NOTIFICATION_ID]

            val channelId = data[NotificationKeys.N_CHANNEL_ID]
            val channelName = data[NotificationKeys.N_CHANNEL_NAME]
            val channelDescription = data[NotificationKeys.N_CHANNEL_DESCRIPTION]

            var channelPriority = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                NotificationManager.IMPORTANCE_MAX
            } else {
                5
            }
            if (data.containsKey(NotificationKeys.N_CHANNEL_PRIORITY)) {
                channelPriority = Integer.parseInt(data[NotificationKeys.N_CHANNEL_PRIORITY]!!)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                setupChannels(channelId!!, channelName!!, channelDescription, channelPriority, applicationContext)
            }

            val notificationId = NOTIFICATION_ID.incrementAndGet()
            val notificationBuilder = NotificationCompat.Builder(applicationContext, channelId!!)
                    .setAutoCancel(true)
                    .setColor(ContextCompat.getColor(applicationContext, R.color.primaryColor))
                    .setSmallIcon(R.mipmap.ic_launcher_round)

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
                when (channelPriority) {
                    NotificationManager.IMPORTANCE_MAX -> notificationBuilder.priority = NotificationCompat.PRIORITY_MAX
                    NotificationManager.IMPORTANCE_HIGH -> notificationBuilder.priority = NotificationCompat.PRIORITY_HIGH
                    NotificationManager.IMPORTANCE_DEFAULT -> notificationBuilder.priority = NotificationCompat.PRIORITY_DEFAULT
                    NotificationManager.IMPORTANCE_LOW -> notificationBuilder.priority = NotificationCompat.PRIORITY_LOW
                }
            }

            if (description != null) {
                notificationBuilder.setContentText(description)
            }
            if (title != null) {
                notificationBuilder.setContentTitle(title)
            }

            if (!CommonUtil.textIsEmpty(uriString) && !CommonUtil.textIsEmpty(notification_id)) {
                notificationBuilder.setStyle(NotificationCompat.BigTextStyle().bigText(description))
                        .setContentIntent(getContentIntent(uriString, notification_id!!, -1, applicationContext))

            }
            if (image != null) {
//                notificationBuilder.setLargeIcon(image)
            }

            notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (notificationManager != null) {
                notificationManager?.notify(notificationId, notificationBuilder.build())
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun setupChannels(channel_id: String, channelName: String, channelDescription: String?, priority: Int, context: Context) {
        val adminChannel = NotificationChannel(channel_id, channelName, priority)
        adminChannel.enableLights(true)
        adminChannel.lightColor = Color.RED
        adminChannel.enableVibration(true)
        val sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()
        adminChannel.setSound(sound, audioAttributes)

        if (channelDescription != null) {
            adminChannel.description = channelDescription
        }
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(adminChannel)
    }

    private fun getContentIntent(uri: String?, notificationId: String?, notificationIdInt: Int, context: Context): PendingIntent {
        val bundle = Bundle()
        bundle.putString(BundleConstants.NOTIFICATION_ID, notificationId)
        bundle.putString(BundleConstants.NOTIFICATION_URI, uri)

        val intent = Intent(context, AuntyActivity::class.java)
        if (!CommonUtil.textIsEmpty(uri)) {
            intent.data = Uri.parse(uri)
        }
        intent.putExtra(IntentConstants.NOTIFICATION_TAPPED, bundle)
        intent.putExtra(IntentConstants.NOTIFICATION_DISMISS_ID, notificationIdInt)
        intent.action = IntentConstants.NOTIFICATION_URI
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        return PendingIntent.getActivity(context, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT)
    }

    override fun onNewToken(s: String?) {
        super.onNewToken(s)
    }
}
