package com.sillylife.plankhana.widgets

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.sillylife.plankhana.R
import com.sillylife.plankhana.utils.CommonUtil

class CustomBottomSheetDialog(
    var resource: Int,
    var title: String,
    var subTitle: String,
    var cancelable: Boolean?,
    var inflater: LayoutInflater,
    var ct: Context,
    var showDone: Boolean,
    var showCancel: Boolean,
    var doneTxt: String,
    var cancelTxt: String,
    var listener: Listener
) : BottomSheetDialog(ct, R.style.BottomSheetDialog) {

    interface Listener {
        fun onDone(view: CustomBottomSheetDialog)
        fun onCancel(view: CustomBottomSheetDialog)
    }

    init {
        setView()
    }

    var dialogTitle: TextView? = null
    var dialogSubTitle: TextView? = null

    fun setView() {
        var sheetView = inflater.inflate(resource, null)
        dialogTitle = sheetView.findViewById(R.id.dialogTitle)
        dialogSubTitle = sheetView.findViewById(R.id.dialogSubTitle)
        var done: MaterialButton = sheetView.findViewById(R.id.done)
        var cancel: MaterialButton = sheetView.findViewById(R.id.cancel)
        dialogTitle?.text = title

        if (!CommonUtil.textIsEmpty(doneTxt)) {
            done.text = doneTxt
        }

        if (!CommonUtil.textIsEmpty(cancelTxt)) {
            cancel.text = cancelTxt
        }

        if (done != null) {
            done.setOnClickListener {
                listener.onDone(this)
            }
            if (!showDone) {
                done.visibility = View.GONE
            }
        }

        if (cancel != null) {
            cancel.setOnClickListener {
                listener.onCancel(this)
            }
            if (!showCancel) {
                cancel.visibility = View.GONE
            }
        }

        if (!CommonUtil.textIsEmpty(subTitle)) {
            dialogSubTitle?.text = subTitle
            dialogSubTitle?.visibility = View.VISIBLE
        }

        setContentView(sheetView)
        setCancelable(cancelable!!)
    }
}