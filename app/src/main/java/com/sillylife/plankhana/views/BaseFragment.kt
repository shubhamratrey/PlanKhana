package com.sillylife.plankhana.views

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.sillylife.plankhana.R
import com.sillylife.plankhana.aunty_side.activities.AuntyActivity
import com.sillylife.plankhana.bhaiya_side.activities.BhaiyaActivity
import com.sillylife.plankhana.registration.activities.RegistrationActivity
import com.sillylife.plankhana.widgets.CustomBottomSheetDialog

open class BaseFragment : Fragment() {

    private var mContext: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = context
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun showToast(message: String, length: Int) {
        if (activity != null && !activity?.isFinishing!!) {
            Toast.makeText(activity, message, length).show()
        }

    }

    fun addFragment(fragment: Fragment, tag: String) {
        if (activity != null && !activity?.isFinishing!!) {
            when (activity) {
                is RegistrationActivity -> (activity as RegistrationActivity).addFragment(fragment, tag)
                is AuntyActivity -> (activity as AuntyActivity).addFragment(fragment, tag)
                is BhaiyaActivity -> (activity as BhaiyaActivity).addFragment(fragment, tag)
            }
        }
    }

    fun replaceFragment(fragment: Fragment, tag: String) {
        if (activity != null && !activity?.isFinishing!!) {
            when (activity) {
                is RegistrationActivity -> (activity as RegistrationActivity).replaceFragment(fragment, tag)
                is AuntyActivity -> (activity as AuntyActivity).replaceFragment(fragment, tag)
                is BhaiyaActivity -> (activity as BhaiyaActivity).replaceFragment(fragment, tag)
            }
        }
    }

    fun showPermissionRequiredDialog(title: String) {
        CustomBottomSheetDialog(R.layout.bs_dialog_alert, title, "", true, layoutInflater, activity!!, true, false, getString(android.R.string.ok), "",
            object : CustomBottomSheetDialog.Listener {
                override fun onDone(view: CustomBottomSheetDialog) {
                    val intent = Intent()
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    val uri = Uri.fromParts("package", context!!.packageName, null)
                    intent.data = uri
                    startActivity(intent)
                    view.dismiss()
                }

                override fun onCancel(view: CustomBottomSheetDialog) {
                    view.dismiss()
                }
            }).show()
    }
}
