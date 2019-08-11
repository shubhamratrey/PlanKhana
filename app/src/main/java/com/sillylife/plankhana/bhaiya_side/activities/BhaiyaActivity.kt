package com.sillylife.plankhana.bhaiya_side.activities

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.sillylife.plankhana.R
import com.sillylife.plankhana.bhaiya_side.fragments.BhaiyaHomeFragment
import com.sillylife.plankhana.utils.FragmentHelper
import com.sillylife.plankhana.views.BaseActivity

class BhaiyaActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bhaiya)
        FragmentHelper.replace(R.id.container, supportFragmentManager, BhaiyaHomeFragment.newInstance(), BhaiyaHomeFragment.TAG)
    }

    fun addFragment(fragment: Fragment, tag: String) {
        if (isFinishing) {
            return
        }
        FragmentHelper.add(R.id.container, supportFragmentManager, fragment, tag)
    }
}
