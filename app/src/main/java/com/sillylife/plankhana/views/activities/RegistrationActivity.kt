package com.sillylife.plankhana.views.activities

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.sillylife.plankhana.R
import com.sillylife.plankhana.models.User
import com.sillylife.plankhana.views.fragments.AddBhaiyaFragment
import com.sillylife.plankhana.views.fragments.FindOrRegisterFragment
import com.sillylife.plankhana.views.fragments.SelectBhaiyaFragment
import com.sillylife.plankhana.utils.FragmentHelper


class RegistrationActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        FragmentHelper.replace(R.id.container, supportFragmentManager, FindOrRegisterFragment.newInstance(), FindOrRegisterFragment.TAG)

        val imageUrl = "https://media-doselect.s3.amazonaws.com/avatar_image/1V4XB5PzwqAqJV2aoKw3QnVyM/download.png"

        val list: ArrayList<User> = ArrayList()
        list.add(User(0, "SHubh", imageUrl))
        list.add(User(0, "SHubh", imageUrl))
        list.add(User(0, "SHubh", imageUrl))
        list.add(User(0, "SHubh", imageUrl))
        //userImages.setUserImage(list)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val fragment = supportFragmentManager.findFragmentById(R.id.container)
        if (fragment is AddBhaiyaFragment) {
            fragment.onActivityResult(requestCode, resultCode, data)
        } else if (fragment is SelectBhaiyaFragment) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }
    }
}
