package com.sillylife.plankhana.views.activities

import android.content.Intent
import android.os.Bundle
import com.sillylife.plankhana.R
import com.sillylife.plankhana.enums.UserType
import com.sillylife.plankhana.managers.sharedpreference.SharedPreferenceManager

class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        redirect()
    }

    private fun redirect() {
        val intent = getRedirectIntent()
        if (intent != null) {
            startActivity(intent)
            finish()
        }
    }

    private fun getRedirectIntent(): Intent? {
        val spm = SharedPreferenceManager
        return if (spm.getHouseId()!! == -1 || spm.isUserRegistrationRequired()!! || spm.getUserType() == null) {
            Intent(this, RegistrationActivity::class.java)
        } else if (spm.getUserType() == UserType.RESIDENT) {
            Intent(this, BhaiyaActivity::class.java)
        } else if (spm.getUserType() == UserType.COOK) {
            Intent(this, AuntyActivity::class.java)
        } else {
            null
        }
    }
}
