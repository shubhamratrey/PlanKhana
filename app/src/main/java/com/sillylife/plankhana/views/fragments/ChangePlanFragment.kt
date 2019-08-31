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
import com.sillylife.plankhana.DeleteUserDishWeekPlanMutation
import com.sillylife.plankhana.GetDayOfWeekQuery
import com.sillylife.plankhana.InsertUserDishWeekPlanMutation
import com.sillylife.plankhana.R
import com.sillylife.plankhana.managers.LocalDishManager
import com.sillylife.plankhana.managers.sharedpreference.SharedPreferenceManager
import com.sillylife.plankhana.models.Dish
import com.sillylife.plankhana.models.User
import com.sillylife.plankhana.services.ApolloService
import com.sillylife.plankhana.services.AppDisposable
import com.sillylife.plankhana.type.Plankhana_users_userdishweekplan_insert_input
import com.sillylife.plankhana.utils.CommonUtil
import com.sillylife.plankhana.utils.MapObjects
import com.sillylife.plankhana.utils.rxevents.RxBus
import com.sillylife.plankhana.utils.rxevents.RxEvent
import com.sillylife.plankhana.utils.rxevents.RxEventType
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
    val toBeDeletingDishesIds: ArrayList<Int> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(context).inflate(R.layout.fragment_change_plan, null, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appDisposable.add(RxBus.listen(RxEvent.Action::class.java).subscribe { action ->
            if (isAdded) {
                when (action.eventType) {
                    RxEventType.CHANGE_PLAN_LIST_DISH_ADD -> {
                        val dish = action.items[0] as Dish?
                        if (rcv.adapter != null && dish != null) {
                            val adapter = rcv.adapter as DishesAdapter
                            adapter.addDishData(dish)
                        }
                    }
                    RxEventType.CHANGE_PLAN_LIST_DISH_REMOVE -> {
                        val dish = action.items[0] as Dish?
                        if (rcv.adapter != null && dish != null) {
                            val adapter = rcv.adapter as DishesAdapter
                            adapter.removeItem(dish)
                        }
                    }
                }
            }
        })

        houseId = SharedPreferenceManager.getHouseId()!!
        user = SharedPreferenceManager.getUser()

        progress?.visibility = View.VISIBLE
        nextBtn.text = getString(R.string.string_continue)

        nextBtn.setOnClickListener {
            getDayOfWeekQuery()
        }

        closeBtn.setOnClickListener {
            fragmentManager?.popBackStack()
        }

        setAdapter(LocalDishManager.getResidentDishes())
    }

    fun getDayOfWeekQuery() {
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
                            if (toBeDeletingDishesIds.size > 0) {
                                deleteDishes(response.data()?.plankhana_users_planweekday()!![0]?.id()!!)
                            }
                            if (LocalDishManager.getTempSavedDishesIds().size > 0) {
                                addDishes(response.data()?.plankhana_users_planweekday()!![0]?.id()!!)
                            }
                        }
                    }
                })
    }

    fun addDishes(weekDayId: Int) {
        val dishes: ArrayList<Plankhana_users_userdishweekplan_insert_input> = ArrayList()
        LocalDishManager.getTempSavedDishesIds().forEach {
            dishes.add(MapObjects.addDishes(houseId, it, user?.id!!, weekDayId))
        }

        val keyMutation = InsertUserDishWeekPlanMutation.builder()
                .insertDishes(dishes)
                .languageId(user?.languageId!!)
                .build()

        ApolloService.buildApollo().mutate(keyMutation)?.enqueue(object :
                ApolloCall.Callback<InsertUserDishWeekPlanMutation.Data>() {
            override fun onFailure(error: ApolloException) {
                Log.d(TAG, error.toString())
            }

            override fun onResponse(@NotNull response: Response<InsertUserDishWeekPlanMutation.Data>) {
                if (isAdded) {
                    response.data()
                }
            }
        })
    }

    fun deleteDishes(weekDayId: Int) {
        val keyMutation = DeleteUserDishWeekPlanMutation.builder()
                .dishIds(toBeDeletingDishesIds)
                .houseId(houseId)
                .userId(user?.id!!)
                .weekdayId(weekDayId)
                .build()

        ApolloService.buildApollo().mutate(keyMutation)?.enqueue(object :
                ApolloCall.Callback<DeleteUserDishWeekPlanMutation.Data>() {
            override fun onFailure(error: ApolloException) {
                Log.d(TAG, error.toString())
            }

            override fun onResponse(@NotNull response: Response<DeleteUserDishWeekPlanMutation.Data>) {
                if (isAdded) {
                    response.data()
                }
            }
        })
    }

    private fun setAdapter(list: ArrayList<Dish>?) {
        if (list != null) {
            val adapter = DishesAdapter(context!!, list) { any, type, pos ->
                if (any is Int && any == DishesAdapter.ADD_DISH_BTN) {
                    addFragment(AddDishFragment.newInstance(), AddDishFragment.TAG)
                } else if (any is Dish && type.contains(DishesAdapter.REMOVE)) {
                    val adapter = rcv.adapter as DishesAdapter
                    adapter.removeItem(any)
                    val dishIds = LocalDishManager.getSavedDishesIds()
                    if (dishIds.contains(any.id!!)) {
                        toBeDeletingDishesIds.add(any.id!!)
                        LocalDishManager.removeDish(any)
                    } else if (LocalDishManager.getTempDishList().size > 0) {
                        LocalDishManager.removeTempDish(any)
                    }
                }
            }
            adapter.setType(DishesAdapter.Add_A_DISH)
            rcv?.layoutManager = LinearLayoutManager(context!!)
            if (rcv?.itemDecorationCount == 0) {
                rcv?.addItemDecoration(ItemDecorator(0, CommonUtil.dpToPx(20), CommonUtil.dpToPx(20), 0, 0))
            }
            progress?.visibility = View.GONE
            rcv?.adapter = adapter
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (LocalDishManager.getTempDishList().size > 0) {
            LocalDishManager.clearTempDishList()
        }
        appDisposable.dispose()
    }
}
