package com.sillylife.plankhana.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.sillylife.plankhana.R
import com.sillylife.plankhana.services.AppDisposable
import com.sillylife.plankhana.views.activities.MainActivity
import kotlinx.android.synthetic.main.fragment_find_or_register.*

class FindOrRegisterFragment : BaseFragment() {

    companion object {
        var TAG = FindOrRegisterFragment::class.java.simpleName
        fun newInstance() = FindOrRegisterFragment()
    }

    var appDisposable: AppDisposable = AppDisposable()
    private var isRegistered: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(context).inflate(R.layout.fragment_find_or_register, null, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setView()
    }

    fun setView() {
        if (isRegistered) {
            stepProgressTv.visibility = View.GONE
            stepProgressLl.visibility = View.GONE

            nameEt.setTitleHint(getString(R.string.enter_your_house_key))
            nextBtn.text = getString(R.string.find)
            headerTv.text = getString(R.string.find_your_house)

            bottomText.text = getString(R.string.don_t_have_a_house_key)
            bottomSubtext.text = getString(R.string.register)

        } else {
            stepProgressTv.visibility = View.VISIBLE
            stepProgressLl.visibility = View.VISIBLE

            nameEt.setTitleHint(getString(R.string.enter_a_unique_house_key))
            nextBtn.text = getString(R.string.generate)
            headerTv.text = getString(R.string.register_your_house)

            bottomText.text = getString(R.string.already_have_a_house_key)
            bottomSubtext.text = getString(R.string.find_your_house)
        }

        findOrRegisterBtn.setOnClickListener {
            isRegistered = !isRegistered
            setView()
        }

        nextBtnProgress.visibility = View.GONE
        nextBtn.setOnClickListener {
            nextBtnProgress.visibility = View.VISIBLE
            nextBtn.text = ""
            openAddUserFragment("")
        }
    }

    fun openAddUserFragment(key: String) {
        if (activity is MainActivity && !isRegistered) {
            setView()
            (activity as MainActivity).addFragment(AddUsersFragment.newInstance(key), AddUsersFragment.TAG)
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        appDisposable.dispose()
    }
}
