package com.sillylife.plankhana.registration.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.fetcher.ApolloResponseFetchers
import com.sillylife.plankhana.AddHouseKeyMutation
import com.sillylife.plankhana.R
import com.sillylife.plankhana.SearchHouseKeyQuery
import com.sillylife.plankhana.services.ApolloService
import com.sillylife.plankhana.views.BaseFragment
import kotlinx.android.synthetic.main.fragment_find_or_register.*
import org.jetbrains.annotations.NotNull

class FindOrRegisterFragment : BaseFragment() {

    companion object {
        var TAG = FindOrRegisterFragment::class.java.simpleName
        fun newInstance() = FindOrRegisterFragment()
    }

    private var isRegistered: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(context).inflate(R.layout.fragment_find_or_register, null, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setView()
    }

    private fun setView() {
        if (!isAdded) {
            return
        }
        if (isRegistered) {
            stepProgressTv?.visibility = View.GONE
            stepProgressLl?.visibility = View.GONE

            nameEt?.setTitleHint(getString(R.string.enter_your_house_key))
            nextBtn?.text = getString(R.string.find)
            headerTv?.text = getString(R.string.find_your_house)

            bottomText?.text = getString(R.string.don_t_have_a_house_key)
            bottomSubtext?.text = getString(R.string.register)

        } else {
            stepProgressTv?.visibility = View.VISIBLE
            stepProgressLl?.visibility = View.VISIBLE

            nameEt?.setTitleHint(getString(R.string.enter_a_unique_house_key))
            nextBtn?.text = getString(R.string.generate)
            headerTv?.text = getString(R.string.register_your_house)

            bottomText?.text = getString(R.string.already_have_a_house_key)
            bottomSubtext?.text = getString(R.string.find_your_house)
        }

        findOrRegisterBtn?.setOnClickListener {
            isRegistered = !isRegistered
            setView()
        }

        nextBtnProgress?.visibility = View.GONE
        nextBtn?.setOnClickListener {
            if (nameEt.mInputEt?.text?.length!! > 3) {
                nextBtnProgress?.visibility = View.VISIBLE
                nextBtn?.text = ""
                searchHouseKey(nameEt.mInputEt?.text.toString())
            } else {
                showToast("Please enter more then 3 letters", Toast.LENGTH_SHORT)
            }
        }
        nameEt?.loginClick {
            isRegistered = !isRegistered
            setView()
        }
        nameEt?.setNormalStateAll()
        nameEt?.setTitle("")
        nameEt?.toggleLoginVisibility(false, "")
    }

    /**
     * isRegistered that means he/she finding house key
     */
    private fun searchHouseKey(key: String) {
        val query = SearchHouseKeyQuery.builder().key(key).build()
        ApolloService.buildApollo().query(query)
                .responseFetcher(ApolloResponseFetchers.NETWORK_ONLY)
                .enqueue(object : ApolloCall.Callback<SearchHouseKeyQuery.Data>() {
                    override fun onFailure(error: ApolloException) {
                        Log.d(TAG, error.toString())
                    }

                    override fun onResponse(@NotNull response: Response<SearchHouseKeyQuery.Data>) {
                        if (!isAdded) {
                            return
                        }

                        activity?.runOnUiThread {
                            if (isRegistered) {
                                if (mutableListOf(response.data())[0]?.plankhana_houses_house()?.size!! >= 1) {
                                    replaceFragment(SelectRoleFragment.newInstance(), AddBhaiyaFragment.TAG)
                                } else {
                                    nameEt.setErrorState()
                                    nameEt.setTitleHint(getString(R.string.new_house_key))
                                    nextBtnProgress?.visibility = View.GONE
                                    nextBtn?.text = getString(R.string.find)
                                    nameEt?.toggleLoginVisibility(true, getString(R.string.generate_now))
                                }
                            } else {
                                if (mutableListOf(response.data())[0]?.plankhana_houses_house()?.size!! >= 1) {
                                    nameEt.setErrorState()
                                    nameEt.setTitleHint(getString(R.string.house_key_already_exists))
                                    nextBtnProgress?.visibility = View.GONE
                                    nextBtn?.text = getString(R.string.generate)
                                    nameEt?.toggleLoginVisibility(true, getString(R.string.login_now))
                                } else {
                                    addHouseKey(key)
                                }
                            }
                        }
                    }
                })
    }

    private fun addHouseKey(key: String) {
        val keyMutation = AddHouseKeyMutation.builder().key(key).build()
        ApolloService.buildApollo().mutate(keyMutation)?.enqueue(object :
                ApolloCall.Callback<AddHouseKeyMutation.Data>() {
            override fun onFailure(error: ApolloException) {
                Log.d(TAG, error.toString())
            }

            override fun onResponse(@NotNull response: Response<AddHouseKeyMutation.Data>) {
                if (isAdded) {
                    replaceFragment(AddBhaiyaFragment.newInstance(), AddBhaiyaFragment.TAG)
                }
            }
        })
    }
}
