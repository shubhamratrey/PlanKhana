package com.sillylife.plankhana.aunty_side.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.fetcher.ApolloResponseFetchers
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.sillylife.plankhana.GetHouseDishesListQuery
import com.sillylife.plankhana.GetHouseResidentListQuery
import com.sillylife.plankhana.R
import com.sillylife.plankhana.enums.UserType
import com.sillylife.plankhana.enums.WeekDay
import com.sillylife.plankhana.managers.sharedpreference.SharedPreferenceManager
import com.sillylife.plankhana.models.Dish
import com.sillylife.plankhana.models.User
import com.sillylife.plankhana.registration.fragments.SelectBhaiyaFragment
import com.sillylife.plankhana.services.ApolloService
import com.sillylife.plankhana.services.AppDisposable
import com.sillylife.plankhana.utils.DateUtil
import com.sillylife.plankhana.views.BaseFragment
import com.sillylife.plankhana.views.adapter.HouseDishesAdapter
import com.sillylife.plankhana.views.adapter.UserListAdapter
import kotlinx.android.synthetic.main.bs_user_list.view.*
import kotlinx.android.synthetic.main.fragment_aunty_home.*
import org.jetbrains.annotations.NotNull

class AuntyHomeFragment : BaseFragment() {

    companion object {
        fun newInstance() = AuntyHomeFragment()
        var TAG = AuntyHomeFragment::class.java.simpleName
    }

    var appDisposable: AppDisposable = AppDisposable()
    val userList: ArrayList<User> = ArrayList()
    var houseId = -1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(context).inflate(R.layout.fragment_aunty_home, null, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        houseId = SharedPreferenceManager.getHouseId()!!

        setHouseResidents()
        getHouseDishes(WeekDay.MONDAY)
        nextBtn.setOnClickListener {
            showUserList(userList)
        }

        DateUtil.today()
    }

    private fun getHouseDishes(weekDay: WeekDay) {
        progress?.visibility = View.VISIBLE
        val list: ArrayList<Dish> = ArrayList()
        val query = GetHouseDishesListQuery.builder().houseId(houseId).dayOfWeek(weekDay.day).build()
        ApolloService.buildApollo().query(query)
                .responseFetcher(ApolloResponseFetchers.NETWORK_ONLY)
                .enqueue(object : ApolloCall.Callback<GetHouseDishesListQuery.Data>() {
                    override fun onFailure(error: ApolloException) {
                        Log.d(SelectBhaiyaFragment.TAG, error.toString())
                    }

                    override fun onResponse(@NotNull response: Response<GetHouseDishesListQuery.Data>) {
                        if (!isAdded) {
                            return
                        }
                        for (dishes in response.data()?.plankhana_users_userdishweekplan_aggregate()?.nodes()?.toMutableList()!!) {
                            val userList: ArrayList<User> = ArrayList()
                            userList.clear()
                            for (users in dishes.dishes_dish().users_userdishweekplans().toMutableList()) {
                                userList.add(User(users.users_userprofile().id(), users.users_userprofile().username(), users.users_userprofile().display_picture()))
                            }
                            list.add(Dish(dishes.dishes_dish().id(), dishes.dishes_dish().dish_name(), dishes.dishes_dish().dish_image(), userList))
                        }
                        activity?.runOnUiThread {
                            setAdapter(list)
                        }
                    }
                })
    }

    fun setAdapter(list: ArrayList<Dish>?) {
        if (list != null) {
            val adapter = HouseDishesAdapter(context!!, list) { any, pos ->

            }
            rcv?.layoutManager = HouseDishesAdapter.WrapContentGridLayoutManager(context!!, 3)
            rcv?.addItemDecoration(HouseDishesAdapter.GridItemDecoration(context?.resources?.getDimensionPixelSize(R.dimen.dp_15)!!, 3))
            progress?.visibility = View.GONE
            rcv?.adapter = adapter
        }
    }

    private fun setHouseResidents() {
        val query = GetHouseResidentListQuery.builder().houseId(houseId).userType(UserType.RESIDENT.type).build()
        ApolloService.buildApollo().query(query)
                .responseFetcher(ApolloResponseFetchers.NETWORK_ONLY)
                .enqueue(object : ApolloCall.Callback<GetHouseResidentListQuery.Data>() {
                    override fun onFailure(error: ApolloException) {
                        Log.d(TAG, error.toString())
                    }

                    override fun onResponse(@NotNull response: Response<GetHouseResidentListQuery.Data>) {
                        if (!isAdded) {
                            return
                        }
                        response.data()?.plankhana_houses_houseuser()?.toMutableList()
                        for (user in response.data()?.plankhana_houses_houseuser()?.toMutableList()!!) {
                            userList.add(User(user.users_userprofile().id(), user.users_userprofile().username(), user.users_userprofile().display_picture()))
                        }
                    }
                })
    }

    private fun showUserList(users: ArrayList<User>) {
        val bottomSheet = BottomSheetDialog(context!!, R.style.BottomSheetDialog)
        val sheetView = layoutInflater.inflate(R.layout.bs_user_list, null)

        val adapter = UserListAdapter(context!!, users) {
            bottomSheet.dismiss()
        }

        val recyclerView = sheetView?.findViewById<RecyclerView>(R.id.rcv)
        recyclerView?.layoutManager = LinearLayoutManager(context)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.adapter = adapter
        bottomSheet.setContentView(sheetView)
        bottomSheet.show()
        bottomSheet.setOnDismissListener {

        }

        sheetView.closeBtn.setOnClickListener {
            bottomSheet.dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        appDisposable.dispose()
    }
}
