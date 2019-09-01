package com.sillylife.plankhana.views.activities

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.sillylife.plankhana.R
import com.sillylife.plankhana.views.fragments.AuntyHomeFragment
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

    fun replaceFragment(fragment: Fragment, tag: String) {
        if (isFinishing) {
            return
        }
        FragmentHelper.replace(R.id.container, supportFragmentManager, fragment, tag)
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentByTag(AuntyHomeFragment.TAG) as AuntyHomeFragment
        if (!fragment.closeExpandedImage()){

        } else {
            super.onBackPressed()
        }
    }
}
