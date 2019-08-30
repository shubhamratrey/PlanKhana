package com.sillylife.plankhana.views.activities

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.sillylife.plankhana.R
import com.sillylife.plankhana.utils.FragmentHelper
import com.sillylife.plankhana.views.fragments.WebViewFragment

class WebActivity : BaseActivity() {

    private var FORM_LINK: String = "https://docs.google.com/forms/d/e/1FAIpQLSfbc_HduyBxmGnfpc4zrPGVMFRGIeAVHvZf8mPzuAtSc6KUOg/viewform?usp=sf_link"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bhaiya)
        FragmentHelper.replace(R.id.container, supportFragmentManager, WebViewFragment.newInstance(FORM_LINK), WebViewFragment.TAG)
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
}
