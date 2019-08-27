package com.sillylife.plankhana.views.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.sillylife.plankhana.R
import com.sillylife.plankhana.views.activities.AuntyActivity
import com.sillylife.plankhana.services.AppDisposable
import kotlinx.android.synthetic.main.fragment_select_role.*
import kotlinx.android.synthetic.main.layout_bottom_button.*

class SelectRoleFragment : BaseFragment() {

    companion object {
        var TAG = SelectRoleFragment::class.java.simpleName
        fun newInstance() = SelectRoleFragment()
    }

    var appDisposable: AppDisposable = AppDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(context).inflate(R.layout.fragment_select_role, null, false)
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

        nextBtn.text = getString(R.string.proceed)
        nextBtn.setOnClickListener {
            if (cooking.isSelected) {
                val intent = Intent(activity, AuntyActivity::class.java)
                startActivity(intent)
                activity?.finish()
            } else if (dining.isSelected) {
                addFragment(SelectBhaiyaFragment.newInstance(), SelectBhaiyaFragment.TAG)
            } else {
                showToast("Please select your role", Toast.LENGTH_SHORT)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        appDisposable.dispose()
    }
}
