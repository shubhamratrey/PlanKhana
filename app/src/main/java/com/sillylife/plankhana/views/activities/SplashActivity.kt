package com.sillylife.plankhana.views.activities

import android.content.Intent
import android.os.Bundle
import com.sillylife.plankhana.R
import com.sillylife.plankhana.managers.sharedpreference.SharedPreferenceManager


class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        redirect()
    }

    private fun redirect() {
        startActivity(getRedirectIntent())
        finish()
    }

    private fun getRedirectIntent(): Intent {
        val spm = SharedPreferenceManager
        if (spm.getHouseId()!! == -1) {
            return Intent(this, RegistrationActivity::class.java)
        } else if (spm.getUser() != null) {
            return Intent(this, BhaiyaActivity::class.java)
        } else {
            return Intent(this, AuntyActivity::class.java)
        }
    }
}
