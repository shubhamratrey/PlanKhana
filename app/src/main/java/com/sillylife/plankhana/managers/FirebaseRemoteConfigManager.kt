package com.sillylife.plankhana.managers

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.sillylife.plankhana.BuildConfig
import com.sillylife.plankhana.R
import com.sillylife.plankhana.constants.Constants


object FirebaseRemoteConfigManager {

    private var remoteConfig: FirebaseRemoteConfig? = null

    init {
        init()
    }

    private fun init() {
        remoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setDeveloperModeEnabled(BuildConfig.DEBUG)
            .build()
        remoteConfig!!.setConfigSettings(configSettings)
        remoteConfig!!.setDefaults(R.xml.remote_config_defaults)
        fetchRemoteConfig()
    }

    private fun fetchRemoteConfig() {
        if (remoteConfig == null) {
            init()
        }
        var cacheExpiration = Constants.FIREBASE_REMOTE_CONFIG_CACHE_EXPIRATION

        if (remoteConfig!!.info.configSettings.isDeveloperModeEnabled) {
            cacheExpiration = 0
        }

        remoteConfig!!.fetch(cacheExpiration).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                remoteConfig!!.activateFetched()
            }
        }
    }

    fun getString(param: String): String {
        if (remoteConfig == null) {
            init()
        }
        return remoteConfig!!.getString(param)
    }

    fun getBoolean(param: String): Boolean {
        if (remoteConfig == null) {
            init()
        }
        return remoteConfig!!.getBoolean(param)
    }

    fun getLong(param: String): Long? {
        if (remoteConfig == null) {
            init()
        }
        return remoteConfig!!.getLong(param)
    }

    fun getDouble(param: String): Double? {
        if (remoteConfig == null) {
            init()
        }
        return remoteConfig!!.getDouble(param)
    }

}