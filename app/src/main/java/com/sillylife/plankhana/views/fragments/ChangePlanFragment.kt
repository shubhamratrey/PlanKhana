package com.sillylife.plankhana.views.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.fetcher.ApolloResponseFetchers
import com.sillylife.plankhana.GetDayOfWeekQuery
import com.sillylife.plankhana.GetHouseUserDishesListQuery
import com.sillylife.plankhana.R
import com.sillylife.plankhana.managers.LocalDishManager
import com.sillylife.plankhana.managers.sharedpreference.SharedPreferenceManager
import com.sillylife.plankhana.models.Dish
import com.sillylife.plankhana.models.DishStatus
import com.sillylife.plankhana.models.User
import com.sillylife.plankhana.services.ApolloService
import com.sillylife.plankhana.services.AppDisposable
import com.sillylife.plankhana.type.Plankhana_users_userdishweekplan_insert_input
import com.sillylife.plankhana.utils.CommonUtil
import com.sillylife.plankhana.views.adapter.DishesAdapter
import com.sillylife.plankhana.views.adapter.item_decorator.ItemDecorator
import kotlinx.android.synthetic.main.fragment_change_plan.*
import kotlinx.android.synthetic.main.layout_bottom_button.*
import org.jetbrains.annotations.NotNull


class ChangePlanFragment : BaseFragment() {

    companion object {
        fun newInstance() = ChangePlanFragment()
        var TAG = ChangePlanFragment::class.java.simpleName
    }

    var appDisposable: AppDisposable = AppDisposable()
    private var houseId = -1
    private var user: User? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(context).inflate(R.layout.fragment_change_plan, null, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progress?.visibility = View.VISIBLE

        houseId = SharedPreferenceManager.getHouseId()!!
        user = SharedPreferenceManager.getUser()
//        getDishes()
        nextBtn.text = getString(R.string.string_continue)

        nextBtn.setOnClickListener {

        }

        closeBtn.setOnClickListener {
            fragmentManager?.popBackStack()
        }

        setAdapter(LocalDishManager.getResidentDishes())
        LocalDishManager.setTempDishList(LocalDishManager.getResidentDishes())
    }

    private fun getDishes() {
        progress?.visibility = View.VISIBLE
        val list: ArrayList<Dish> = ArrayList()
        val query = GetHouseUserDishesListQuery.builder()
//                .dayOfWeek(WeekType.TODAY.day)
                .dayOfWeek("monday")
                .houseId(houseId)
                .userId(user?.id!!)
                .languageId(user?.languageId!!)
                .build()

        ApolloService.buildApollo().query(query)
                .responseFetcher(ApolloResponseFetchers.NETWORK_ONLY)
                .enqueue(object : ApolloCall.Callback<GetHouseUserDishesListQuery.Data>() {
                    override fun onFailure(error: ApolloException) {
                        Log.d(SelectBhaiyaFragment.TAG, error.toString())
                    }

                    override fun onResponse(@NotNull response: Response<GetHouseUserDishesListQuery.Data>) {
                        if (!isAdded) {
                            return
                        }
                        for (dishes in response.data()?.plankhana_users_userdishweekplan()?.toMutableList()!!) {
                            val name = if (dishes.dishes_dish().dishes_dishlanguagenames().size > 0) dishes.dishes_dish().dishes_dishlanguagenames()[0].dish_name() else ""
                            list.add(Dish(dishes.dishes_dish().id(), name, dishes.dishes_dish().dish_image(), DishStatus(added = true)))
                        }
                        activity?.runOnUiThread {
                            setAdapter(list)
                        }
                    }
                })
    }

    fun getDayOfWeekQuery(dishes: List<Plankhana_users_userdishweekplan_insert_input>, dishIds: ArrayList<Int>) {
        val query = GetDayOfWeekQuery.builder()
//                .dayOfWeek(WeekType.TODAY.day)
                .dayOfWeek("monday")
                .build()
        ApolloService.buildApollo().query(query)
                .responseFetcher(ApolloResponseFetchers.NETWORK_ONLY)
                .enqueue(object : ApolloCall.Callback<GetDayOfWeekQuery.Data>() {
                    override fun onFailure(error: ApolloException) {
                        Log.d(SelectBhaiyaFragment.TAG, error.toString())
                    }

                    override fun onResponse(@NotNull response: Response<GetDayOfWeekQuery.Data>) {
                        if (!isAdded) {
                            return
                        }
                        if (mutableListOf(response.data())[0]?.plankhana_users_planweekday()?.size!! >= 1) {
                            addOrDelete(dishes, dishIds, response.data()?.plankhana_users_planweekday()!![0]?.id()!!)
                        }
                    }
                })
    }

    fun addOrDelete(dishes: List<Plankhana_users_userdishweekplan_insert_input>, dishIds: ArrayList<Int>, weekDayId: Int) {
//        val keyMutation = AddDeleteHouseUserDishesMutation.builder()
//                .insertDishes(dishes)
//                .dishIds(dishIds)
//                .userId(user?.id)
//                .houseId(houseId)
//                .weekdayId(weekDayId)
//                .build()
//
//        ApolloService.buildApollo().mutate(keyMutation)?.enqueue(object :
//                ApolloCall.Callback<AddDeleteHouseUserDishesMutation.Data>() {
//            override fun onFailure(error: ApolloException) {
//
//            }
//
//            override fun onResponse(@NotNull response: Response<AddDeleteHouseUserDishesMutation.Data>) {
//                if (isAdded) {
//
//                }
//            }
//        })
    }

    fun setAdapter(list: ArrayList<Dish>?) {
        if (list != null) {
            val adapter = DishesAdapter(context!!, list) { any, pos ->
                if (any is Int && any == DishesAdapter.ADD_DISH_BTN) {
                    addFragment(AddDishFragment.newInstance(), AddDishFragment.TAG)
                }
            }
            adapter.setType(DishesAdapter.Add_A_DISH)
            rcv?.layoutManager = LinearLayoutManager(context!!)
            if (rcv?.itemDecorationCount == 0) {
                rcv?.addItemDecoration(ItemDecorator(0, CommonUtil.dpToPx(20), 0, 0, 0))
            }
            progress?.visibility = View.GONE
            rcv?.adapter = adapter
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        appDisposable.dispose()
    }
}
