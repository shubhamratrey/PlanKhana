package com.sillylife.plankhana.managers

import android.os.Bundle
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.sillylife.plankhana.BuildConfig
import com.sillylife.plankhana.PlanKhana
import com.sillylife.plankhana.constants.BundleConstants
import com.sillylife.plankhana.constants.Constants
import com.sillylife.plankhana.managers.sharedpreference.SharedPreferenceManager

object EventsManager {
    val TAG = EventsManager::class.java.simpleName

    @Volatile
    private var mFirebaseAnalytics: FirebaseAnalytics? = null

    init {
        init()
    }

    @Synchronized
    private fun init() {
        val context = PlanKhana.getInstance()
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context)
        mFirebaseAnalytics!!.setAnalyticsCollectionEnabled(!BuildConfig.DEBUG)
    }

    fun setEventName(eventName: String): EventBuilder {
        val eventBuilder = EventBuilder()
        eventBuilder.setEventName(eventName)
        return eventBuilder
    }

    fun sendEvent(eventName: String, eventBundle: Bundle) {
        sendEvent(eventName, 1.0, eventBundle)
    }

    @Synchronized
    private fun sendEvent(eventName: String, value: Double, eventBundle: Bundle) {
        if (BuildConfig.DEBUG) {
            val stringBuilder = StringBuilder()
            stringBuilder.append("eventName : $eventName").append(" || ")
            for (key in eventBundle.keySet()) {
                stringBuilder.append("$key : ${eventBundle.get(key)}").append(" || ")
            }
            Log.d(TAG, stringBuilder.toString())
        }
        if (!BuildConfig.DEBUG) {
            if (mFirebaseAnalytics == null) {
                init()
            }
            val houseId = SharedPreferenceManager.getHouseId()!!
            if (houseId > 1) {
                eventBundle.putString(BundleConstants.HOUSE_ID, houseId.toString())
            }
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                eventBundle.putString(Constants.USER_ID, user.uid)
                mFirebaseAnalytics?.setUserId(user.uid)
            }

            mFirebaseAnalytics?.logEvent(eventName, eventBundle)
        }
    }

    class EventBuilder {

        private var mBundle = Bundle()
        private var eventName: String = ""
        private var extraValue: Double = 1.0

        fun setEventName(eventName: String) {
            this.eventName = eventName
        }

        fun setExtraValue(value: Double): EventBuilder {
            extraValue = value
            return this
        }

        fun addProperty(key: String, value: Any?): EventBuilder {
            if (value != null) {
                when (value) {
                    is String -> {
                        var a = value.toString()
                        a = a.substring(0, Math.min(a.length, 100))
                        mBundle.putString(key, a)
                    }
                    is Int -> mBundle.putInt(key, value)
                    is Boolean -> mBundle.putBoolean(key, value)
                    is Long -> mBundle.putLong(key, value)
                    is Float -> mBundle.putFloat(key, value)
                }
            } else {
                mBundle.putString(key, "")
            }
            return this
        }

        fun addProperty(key: String, bundle: Bundle): EventBuilder {
            mBundle.putBundle(key, bundle)
            return this
        }

        fun addBundle(bundle: Bundle): EventBuilder {
            mBundle.putAll(bundle)
            return this
        }

        fun send() {
            sendEvent(eventName, extraValue, mBundle)
        }
    }
}