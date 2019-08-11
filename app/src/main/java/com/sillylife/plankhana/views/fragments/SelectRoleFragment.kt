package com.sillylife.plankhana.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.sillylife.plankhana.R
import com.sillylife.plankhana.services.AppDisposable
import com.sillylife.plankhana.views.activities.MainActivity
import kotlinx.android.synthetic.main.fragment_add_role.*

class SelectRoleFragment : BaseFragment() {

    companion object {
        var TAG = SelectRoleFragment::class.java.simpleName
        fun newInstance() = SelectRoleFragment()
    }

    var appDisposable: AppDisposable = AppDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(context).inflate(R.layout.fragment_add_role, null, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dining.setOnClickListener {
            dining.isSelected = true
            cooking.isSelected = false
        }

        cooking.setOnClickListener {
            cooking.isSelected = true
            dining.isSelected = false
        }

        nextBtn.setOnClickListener {
            if (cooking.isSelected) {
                showToast("COOKING", Toast.LENGTH_SHORT)
            } else if (dining.isSelected) {
                addFragment(SelectBhaiyaFragment.newInstance(), SelectBhaiyaFragment.TAG)
            } else {
                showToast("Please select your role", Toast.LENGTH_SHORT)
            }
        }
    }

    fun addFragment(fragment: Fragment, tag: String) {
        if (activity is MainActivity) {
            (activity as MainActivity).addFragment(fragment, tag)
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        appDisposable.dispose()
    }
}
