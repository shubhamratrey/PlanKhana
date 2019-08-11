package com.sillylife.plankhana.views.fragments

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment

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
}
