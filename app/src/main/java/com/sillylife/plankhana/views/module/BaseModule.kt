package com.sillylife.plankhana.views.module

import com.sillylife.plankhana.PlanKhana
import com.sillylife.plankhana.services.AppDisposable
import com.sillylife.plankhana.services.ConnectivityReceiver


open class BaseModule {
    val application = PlanKhana.getInstance()
    val apolloClient= application.getApolloService()
    var appDisposable = AppDisposable()

    fun onDestroy() {
        if (appDisposable != null) {
            appDisposable.dispose()
        }
    }

    fun getDisposable(): AppDisposable {
        if (appDisposable == null) {
            appDisposable = AppDisposable()
        }
        return appDisposable
    }

    fun disposableWithNetworkCheck(networkAvailable: (String) -> Unit): AppDisposable? {
        if (!ConnectivityReceiver.isConnected(application.applicationContext)) {
            //networkAvailable(mKukuFMApplication.getString(R.string.no_internet_connection))
            return null
        }
        return appDisposable
    }

}