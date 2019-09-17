package com.sillylife.plankhana

import android.content.IntentFilter
import android.net.ConnectivityManager
import androidx.multidex.MultiDexApplication
import com.apollographql.apollo.ApolloClient
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import com.sillylife.plankhana.fcm.FCMService
import com.sillylife.plankhana.services.*
import com.sillylife.plankhana.utils.rxevents.RxBus
import com.sillylife.plankhana.utils.rxevents.RxEvent
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import com.google.firebase.analytics.FirebaseAnalytics
import io.fabric.sdk.android.Fabric

class PlanKhana : MultiDexApplication(), ConnectivityReceiverListener {
    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        appDisposable.dispose()
        if (!isConnected) {
            appDisposable.add(Observable.timer(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    RxBus.publish(RxEvent.NetworkConnectivity(isConnected))
                })
        } else {
            RxBus.publish(RxEvent.NetworkConnectivity(isConnected))
        }
    }

    private var mIAPIService: FCMService.FirebaseAPI? = null

    @Volatile
    private var mApolloClient: ApolloClient? = null

    private var connectivityReceiver: ConnectivityReceiver? = null

    var appDisposable: AppDisposable = AppDisposable()

    private var mFirebaseAnalytics: FirebaseAnalytics? = null

    companion object {
        @Volatile
        private var application: PlanKhana? = null

        @Synchronized
        fun getInstance(): PlanKhana {
            return application!!
        }
    }

    override fun onCreate() {
        super.onCreate()
        application = this@PlanKhana

        val intentFilter = IntentFilter()
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        connectivityReceiver = ConnectivityReceiver(this)
        registerReceiver(connectivityReceiver, intentFilter)

        if (!BuildConfig.DEBUG) {
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        }

        if (BuildConfig.IS_FABRIC_REQUIRED) {
            val core = CrashlyticsCore.Builder().disabled(!BuildConfig.IS_FABRIC_REQUIRED).build()
            Fabric.with(this, Crashlytics.Builder().core(core).build())
        }
    }

    @Synchronized
    fun getApolloService(): ApolloClient {
        if (mApolloClient == null) {
            mApolloClient = ApolloService.buildApollo()
        }
        return mApolloClient!!
    }

    @Synchronized
    fun getFCMService(): FCMService.FirebaseAPI {
        if (mIAPIService == null) {
            mIAPIService = FCMService.build()
        }
        return mIAPIService!!
    }

}