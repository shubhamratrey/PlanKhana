package com.sillylife.plankhana.views

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.sillylife.plankhana.aunty_side.activities.AuntyActivity
import com.sillylife.plankhana.bhaiya_side.activities.BhaiyaActivity
import com.sillylife.plankhana.registration.activities.RegistrationActivity

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
}
