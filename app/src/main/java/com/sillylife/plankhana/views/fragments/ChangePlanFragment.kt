package com.sillylife.plankhana.views.fragments

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.fetcher.ApolloResponseFetchers
import com.sillylife.plankhana.*
import com.sillylife.plankhana.enums.WeekType
import com.sillylife.plankhana.managers.LocalDishManager
import com.sillylife.plankhana.managers.sharedpreference.SharedPreferenceManager
import com.sillylife.plankhana.models.Dish
import com.sillylife.plankhana.models.Message
import com.sillylife.plankhana.models.NotifyData
import com.sillylife.plankhana.models.User
import com.sillylife.plankhana.models.responses.EmptyResponse
import com.sillylife.plankhana.services.ApolloService
import com.sillylife.plankhana.services.AppDisposable
import com.sillylife.plankhana.services.CallbackWrapper
import com.sillylife.plankhana.type.Plankhana_users_userdishweekplan_insert_input
import com.sillylife.plankhana.utils.CommonUtil
import com.sillylife.plankhana.utils.MapObjects
import com.sillylife.plankhana.utils.rxevents.RxBus
import com.sillylife.plankhana.utils.rxevents.RxEvent
import com.sillylife.plankhana.utils.rxevents.RxEventType
import com.sillylife.plankhana.views.adapter.DishesAdapter
import com.sillylife.plankhana.views.adapter.item_decorator.ItemDecorator
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_change_plan.*
import kotlinx.android.synthetic.main.layout_bottom_button.*
import org.jetbrains.annotations.NotNull


class ChangePlanFragment : BaseFragment() {

    companion object {
        fun newInstance() = ChangePlanFragment()

        fun newInstance(day: String): ChangePlanFragment {
            val fragment = ChangePlanFragment()
            val args = Bundle()
            args.putString("day", day)
            fragment.arguments = args
            return fragment
        }

        var TAG = ChangePlanFragment::class.java.simpleName
    }

    var appDisposable: AppDisposable = AppDisposable()
    private var houseId = -1
    private var user: User? = null
    private var deleteResponse = false
    private var addResponse = false
    private var day: String = ""
    val toBeDeletingDishesIds: ArrayList<Int> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(context).inflate(R.layout.fragment_change_plan, null, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null && arguments!!.containsKey("day")) {
            day = arguments?.getString("day")!!
        }
        nextBtn?.alpha = 0.7f
        appDisposable.add(RxBus.listen(RxEvent.Action::class.java).subscribe { action ->
            if (isAdded) {
                when (action.eventType) {
                    RxEventType.CHANGE_PLAN_LIST_DISH_ADD -> {
                        val dish = action.items[0] as Dish?
                        if (rcv.adapter != null && dish != null) {
                            val adapter = rcv.adapter as DishesAdapter
                            adapter.addDishData(dish)
                        }
                        toggleBtn()
                    }
                    RxEventType.CHANGE_PLAN_LIST_DISH_REMOVE -> {
                        val dish = action.items[0] as Dish?
                        if (rcv.adapter != null && dish != null) {
                            val adapter = rcv.adapter as DishesAdapter
                            adapter.removeItem(dish)
                        }
                        toggleBtn()
                    }
                }
            }
        })

        houseId = SharedPreferenceManager.getHouseId()!!
        user = SharedPreferenceManager.getUser()

        progress?.visibility = View.VISIBLE
        nextBtn?.text = getString(R.string.string_save)

        nextBtn?.setOnClickListener {
            toggleButtonProgress(true)
            getDayOfWeekQuery(if (CommonUtil.textIsEmpty(day)) WeekType.TODAY.day else day)
        }

        closeBtn.setOnClickListener {
            fragmentManager?.popBackStack()
        }

        setAdapter(LocalDishManager.getResidentDishes())
        toggleBtn()
    }

    private fun toggleButtonProgress(show: Boolean) {
        if (show) {
            nextBtn?.text = ""
            nextBtnProgress?.visibility = View.VISIBLE
            nextBtn.isEnabled = false
        } else {
            nextBtn?.text = getString(R.string.string_save)
            nextBtnProgress?.visibility = View.VISIBLE
            nextBtn.isEnabled = true
        }
    }

    private fun getDayOfWeekQuery(day: String) {
        val query = GetDayOfWeekQuery.builder()
                .dayOfWeek(day)
                .build()
        ApolloService.buildApollo().query(query)
                .responseFetcher(ApolloResponseFetchers.NETWORK_ONLY)
                .enqueue(object : ApolloCall.Callback<GetDayOfWeekQuery.Data>() {
                    override fun onFailure(error: ApolloException) {
                        Log.d(SelectBhaiyaFragment.TAG, error.toString())
                        toggleButtonProgress(false)
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

                            sendNotificationAsString(houseId.toString(), NotifyData(title = "${user?.name!!} भैया ने कुछ बदला है", description = "Please check $day plan", image = ""))
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
                toggleButtonProgress(false)
            }

            override fun onResponse(@NotNull response: Response<InsertUserDishWeekPlanMutation.Data>) {
                if (isAdded) {
                    activity?.runOnUiThread {
                        Log.d(TAG, response.data().toString())
                        addResponse = true
                        finishFragment()
                        LocalDishManager.saveFavouriteDishes(LocalDishManager.getTempDishList())
                    }
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
                toggleButtonProgress(false)
            }

            override fun onResponse(@NotNull response: Response<DeleteUserDishWeekPlanMutation.Data>) {
                if (isAdded) {
                    activity?.runOnUiThread {
                        deleteResponse = true
                        finishFragment()
                    }
                }
            }
        })
    }

    private fun validateList(): Boolean {
        var d = false
        if (toBeDeletingDishesIds.size > 0 && LocalDishManager.getTempSavedDishesIds().size > 0) {
            d = true
        } else if (toBeDeletingDishesIds.size > 0) {
            d = true
        } else if (LocalDishManager.getTempDishList().size > 0) {
            d = true
        }
        return d
    }

    fun finishFragment() {
        toggleButtonProgress(false)
        if (toBeDeletingDishesIds.size > 0 && LocalDishManager.getTempSavedDishesIds().size > 0 && addResponse && deleteResponse) {
            RxBus.publish(RxEvent.Action(RxEventType.REFRESH_DISH_LIST))
            fragmentManager?.popBackStack()
        } else if (toBeDeletingDishesIds.size > 0 && deleteResponse) {
            RxBus.publish(RxEvent.Action(RxEventType.REFRESH_DISH_LIST))
            fragmentManager?.popBackStack()
        } else if (LocalDishManager.getTempSavedDishesIds().size > 0 && addResponse) {
            RxBus.publish(RxEvent.Action(RxEventType.REFRESH_DISH_LIST))
            fragmentManager?.popBackStack()
        }
    }

    fun toggleBtn() {
        activity?.runOnUiThread {
            Handler().postDelayed({
                if (validateList()) {
                    nextBtn.alpha = 1f
                    nextBtn.isEnabled = true
                } else {
                    nextBtn.alpha = 0.7f
                    nextBtn.isEnabled = false
                }
            }, 200)
        }
    }

    private fun setAdapter(list: ArrayList<Dish>?) {
        if (list != null) {
            val adapter = DishesAdapter(context!!, DishesAdapter.Add_A_DISH, list) { any, type, pos ->
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
                    toggleBtn()
                }
            }
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

    fun sendNotificationAsString(to: String, notifyData: NotifyData) {
        appDisposable.add(PlanKhana.getInstance().getFCMService()
                .sendNotification(Message("/topics/$to",86400, notifyData))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribeWith(object : CallbackWrapper<retrofit2.Response<EmptyResponse>>() {
                    override fun onSuccess(t: retrofit2.Response<EmptyResponse>) {
                        Log.d("Response ", "onResponse")
                    }

                    override fun onFailure(code: Int, message: String) {
                        Log.d("Response ", "onFailure")
                    }
                }))
    }
}
