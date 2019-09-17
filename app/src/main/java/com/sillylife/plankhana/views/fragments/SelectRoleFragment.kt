package com.sillylife.plankhana.views.fragments

import android.content.Intent
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
import com.sillylife.plankhana.AddWorkerMutation
import com.sillylife.plankhana.GetHouseResidentListQuery
import com.sillylife.plankhana.R
import com.sillylife.plankhana.constants.EventConstants
import com.sillylife.plankhana.enums.UserType
import com.sillylife.plankhana.managers.EventsManager
import com.sillylife.plankhana.managers.sharedpreference.SharedPreferenceManager
import com.sillylife.plankhana.models.User
import com.sillylife.plankhana.services.ApolloService
import com.sillylife.plankhana.services.AppDisposable
import com.sillylife.plankhana.views.activities.AuntyActivity
import kotlinx.android.synthetic.main.fragment_select_role.*
import kotlinx.android.synthetic.main.layout_bottom_button.*
import org.jetbrains.annotations.NotNull

class SelectRoleFragment : BaseFragment() {

    companion object {
        var TAG = SelectRoleFragment::class.java.simpleName
        fun newInstance() = SelectRoleFragment()
    }

    var appDisposable: AppDisposable = AppDisposable()
    private var houseId = -1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(context).inflate(R.layout.fragment_select_role, null, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        EventsManager.setEventName(EventConstants.SELECT_ROLE_SCREEN_VIEWED).send()
        houseId = SharedPreferenceManager.getHouseId()!!

        dining.setOnClickListener {
            dining.isSelected = true
            cooking.isSelected = false
        }

        cooking.setOnClickListener {
            cooking.isSelected = true
            dining.isSelected = false
        }

        nextBtn.text = getString(R.string.proceed)
        nextBtn.setOnClickListener {
            when {
                cooking.isSelected -> searchHouseWorker()
                dining.isSelected -> addFragment(SelectBhaiyaFragment.newInstance(), SelectBhaiyaFragment.TAG)
                else -> showToast("Please select your role", Toast.LENGTH_SHORT)
            }
        }
    }

    private fun searchHouseWorker() {
        val query = GetHouseResidentListQuery.builder()
                .houseId(houseId)
                .userType(UserType.COOK.type)
                .build()
        ApolloService.buildApollo().query(query)
                .responseFetcher(ApolloResponseFetchers.NETWORK_ONLY)
                .enqueue(object : ApolloCall.Callback<GetHouseResidentListQuery.Data>() {
                    override fun onFailure(error: ApolloException) {
                        Log.d(SelectBhaiyaFragment.TAG, error.toString())
                    }

                    override fun onResponse(@NotNull response: Response<GetHouseResidentListQuery.Data>) {
                        if (!isAdded) {
                            return
                        }
                        if (response.data()?.plankhana_houses_houseuser()?.size!! > 0) {
                            val user = response.data()?.plankhana_houses_houseuser()?.toMutableList()!![0].users_userprofile()
                            SharedPreferenceManager.setUser(User(user.id(), user.language_id()))
                            SharedPreferenceManager.setUserType(UserType.COOK)
                            val intent = Intent(activity, AuntyActivity::class.java)
                            startActivity(intent)
                            activity?.finish()
                        } else {
                            addWorker()
                        }
                    }
                })
    }

    private fun addWorker() {
        val keyMutation = AddWorkerMutation.builder()
                .houseId(houseId)
                .userType(UserType.COOK.type)
                .build()
        ApolloService.buildApollo().mutate(keyMutation)?.enqueue(object :
                ApolloCall.Callback<AddWorkerMutation.Data>() {
            override fun onFailure(error: ApolloException) {
                Log.d(TAG, error.toString())
            }

            override fun onResponse(@NotNull response: Response<AddWorkerMutation.Data>) {
                if (isAdded) {
                    val languageId = response.data()?.insert_plankhana_houses_houseuser()?.returning()!![0].users_userprofile().language_id()
                    val userId = response.data()?.insert_plankhana_houses_houseuser()?.returning()!![0].user_id()
                    SharedPreferenceManager.setUser(User(userId, languageId))
                    SharedPreferenceManager.setUserType(UserType.COOK)
                    val intent = Intent(activity, AuntyActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        appDisposable.dispose()
    }
}
