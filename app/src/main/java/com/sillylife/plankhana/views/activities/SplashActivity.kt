package com.sillylife.plankhana.views.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.sillylife.plankhana.R
import com.sillylife.plankhana.enums.UserType
import com.sillylife.plankhana.managers.AuthManager
import com.sillylife.plankhana.managers.sharedpreference.SharedPreferenceManager

class SplashActivity : BaseActivity() {

    val TAG: String = SplashActivity::class.java.name
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        AuthManager.signInAnonymously(object : AuthManager.IAuthCredentialAnonymouslyLoginCallback {
            override fun onSignInAnonymously(userId: String) {
                Log.d(TAG, userId)
            }
        })
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
