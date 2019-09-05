package com.sillylife.plankhana

import android.content.IntentFilter
import android.net.ConnectivityManager
import androidx.multidex.MultiDexApplication
import com.apollographql.apollo.ApolloClient
import com.sillylife.plankhana.fcm.FCMService
import com.sillylife.plankhana.fcm.FirebaseAPI
import com.sillylife.plankhana.services.*
import com.sillylife.plankhana.utils.rxevents.RxBus
import com.sillylife.plankhana.utils.rxevents.RxEvent
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

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

    private var mIAPIService: FirebaseAPI? = null

    @Volatile
    private var mApolloClient: ApolloClient? = null

    private var connectivityReceiver: ConnectivityReceiver? = null

    var appDisposable: AppDisposable = AppDisposable()

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
    }

    @Synchronized
    fun getApolloService(): ApolloClient {
        if (mApolloClient == null) {
            mApolloClient = ApolloService.buildApollo()
        }
        return mApolloClient!!
    }

    @Synchronized
    fun getFCMService(): FirebaseAPI {
        if (mIAPIService == null) {
            mIAPIService = FCMService.build()
        }
        return mIAPIService!!
    }

}