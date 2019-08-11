package com.sillylife.plankhana.bhaiya_side.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sillylife.plankhana.R
import com.sillylife.plankhana.services.AppDisposable
import com.sillylife.plankhana.views.BaseFragment
import kotlinx.android.synthetic.main.layout_bottom_button.*


class BhaiyaHomeFragment : BaseFragment() {

    companion object {
        fun newInstance() = BhaiyaHomeFragment()
        var TAG = BhaiyaHomeFragment::class.java.simpleName
    }

    var appDisposable: AppDisposable = AppDisposable()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(context).inflate(R.layout.fragment_bhaiya_home, null, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nextBtn.text = getString(R.string.change_plan)

        nextBtn.setOnClickListener {

        }

    }

    override fun onDestroy() {
        super.onDestroy()
        appDisposable.dispose()
    }
}
