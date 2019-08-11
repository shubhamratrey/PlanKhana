package com.sillylife.plankhana.aunty_side.activities

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.sillylife.plankhana.R
import com.sillylife.plankhana.aunty_side.fragments.AuntyHomeFragment
import com.sillylife.plankhana.views.BaseActivity
import com.sillylife.plankhana.utils.FragmentHelper

class AuntyActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aunty)

        FragmentHelper.replace(R.id.container, supportFragmentManager, AuntyHomeFragment.newInstance(), AuntyHomeFragment.TAG)
    }

    fun addFragment(fragment: Fragment, tag: String) {
        if (isFinishing) {
            return
        }
        FragmentHelper.add(R.id.container, supportFragmentManager, fragment, tag)
    }
}
