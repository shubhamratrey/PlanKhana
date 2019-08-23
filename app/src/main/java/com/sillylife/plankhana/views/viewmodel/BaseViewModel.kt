package com.sillylife.plankhana.views.viewmodel

import android.content.Context
import android.os.Handler
import android.view.LayoutInflater
import androidx.lifecycle.ViewModel
import com.sillylife.plankhana.services.AppDisposable
import com.sillylife.plankhana.views.module.BaseModule
import com.sillylife.plankhana.widgets.CustomBottomSheetDialog

abstract class BaseViewModel : ViewModel() {

    private var customBottomSheetDialog: CustomBottomSheetDialog? = null
    //    private var floatingBottomSheetDialog: FloatingBottomSheetDialog? = null
    private var baseModule: BaseModule? = null

    abstract fun setViewModel(): BaseModule

    init {
        init()
    }

    private fun init() {
        Handler().postDelayed({
            baseModule = setViewModel()

        }, 200)
    }

    fun onDestroy() {
        baseModule?.onDestroy()
    }

    fun getAppDisposable(): AppDisposable {
        if (baseModule == null) {
            baseModule = setViewModel()
        }
        return baseModule?.getDisposable()!!
    }

    fun dismissBottomSheetDialog() {
        if (customBottomSheetDialog != null) {
            customBottomSheetDialog?.dismiss()
            customBottomSheetDialog = null
        }
    }

    fun showBottomSheetDialog(
        resource: Int,
        title: String,
        subTitle: String,
        cancelable: Boolean?,
        inflater: LayoutInflater,
        ct: Context,
        showDone: Boolean,
        showCancel: Boolean,
        doneTxt: String,
        cancelTxt: String,
        listener: (Boolean) -> Unit
    ) {
        customBottomSheetDialog = CustomBottomSheetDialog(
            resource,
            title,
            subTitle,
            cancelable,
            inflater,
            ct,
            showDone,
            showCancel,
            doneTxt,
            cancelTxt,
            object : CustomBottomSheetDialog.Listener {
                override fun onDone(view: CustomBottomSheetDialog) {
                    listener(true)
                }

                override fun onCancel(view: CustomBottomSheetDialog) {
                    listener(false)
                }
            })
        customBottomSheetDialog?.show()
    }

//    fun dismissFloatingBottomSheetDialog() {
//        if (floatingBottomSheetDialog != null) {
//            floatingBottomSheetDialog?.dismiss()
//            floatingBottomSheetDialog = null
//        }
//    }

//    fun showFloatingBottomSheetDialog(
//            layout: Int,
//            pictureDialogItems: ArrayList<Any>,
//            layoutInflater: LayoutInflater,
//            context: Context,
//            listener: (Any, Int) -> Unit): FloatingBottomSheetDialog? {
//        var sleepTimerSlug = SharedPreferenceManager.getSleepTimerSlug()
//        floatingBottomSheetDialog = FloatingBottomSheetDialog(layout, pictureDialogItems, layoutInflater, context, sleepTimerSlug.isNotEmpty()) { item, position ->
//            listener(item, position)
//        }
//        floatingBottomSheetDialog?.show()
//        return floatingBottomSheetDialog
//    }
}