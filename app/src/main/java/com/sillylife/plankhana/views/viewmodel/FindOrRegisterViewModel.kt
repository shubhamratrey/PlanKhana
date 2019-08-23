package com.sillylife.plankhana.views.viewmodel

import com.sillylife.plankhana.models.responses.FindOrRegisterResponse
import com.sillylife.plankhana.views.BaseFragment
import com.sillylife.plankhana.views.module.BaseModule
import com.sillylife.plankhana.views.module.FindOrRegisterModule

class FindOrRegisterViewModel(fragment: BaseFragment) : BaseViewModel(), FindOrRegisterModule.IModuleListener {
    override fun onHouseKeyResultApiSuccess(response: FindOrRegisterResponse) {
        iModuleListener.onHouseKeyResultApiSuccess(response)
    }

    override fun onHouseKeyResultApiFailure(error: String) {
        iModuleListener.onHouseKeyResultApiFailure(error)
    }

    override fun onHouseKeyAddedApiSuccess(response: FindOrRegisterResponse) {
        iModuleListener.onHouseKeyAddedApiSuccess(response)
    }

    override fun onHouseKeyAddedApiFailure(error: String) {
        iModuleListener.onHouseKeyAddedApiFailure(error)
    }

    private val module = FindOrRegisterModule(this)
    private val iModuleListener = fragment as FindOrRegisterModule.IModuleListener

    override fun setViewModel(): BaseModule {
        return module
    }

    fun addHouseKey(key: String) {
        module.addHouseKey(key)
    }

    fun getAllHouseKeys(){
        module.searchHouseId()
    }
}
