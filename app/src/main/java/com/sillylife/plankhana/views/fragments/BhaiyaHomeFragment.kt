package com.sillylife.plankhana.views.fragments

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.fetcher.ApolloResponseFetchers
import com.google.firebase.messaging.FirebaseMessaging
import com.sillylife.plankhana.GetHouseUserDishesListQuery
import com.sillylife.plankhana.R
import com.sillylife.plankhana.constants.BundleConstants
import com.sillylife.plankhana.constants.EventConstants
import com.sillylife.plankhana.enums.UserType
import com.sillylife.plankhana.enums.WeekType
import com.sillylife.plankhana.managers.EventsManager
import com.sillylife.plankhana.managers.LocalDishManager
import com.sillylife.plankhana.managers.sharedpreference.SharedPreferenceManager
import com.sillylife.plankhana.models.Dish
import com.sillylife.plankhana.models.DishStatus
import com.sillylife.plankhana.models.User
import com.sillylife.plankhana.services.ApolloService
import com.sillylife.plankhana.services.AppDisposable
import com.sillylife.plankhana.utils.CommonUtil
import com.sillylife.plankhana.utils.OnSwipeTouchListener
import com.sillylife.plankhana.utils.rxevents.RxBus
import com.sillylife.plankhana.utils.rxevents.RxEvent
import com.sillylife.plankhana.utils.rxevents.RxEventType
import com.sillylife.plankhana.views.adapter.HouseDishesAdapter
import com.sillylife.plankhana.views.adapter.item_decorator.GridItemDecoration
import com.sillylife.plankhana.views.adapter.item_decorator.WrapContentGridLayoutManager
import kotlinx.android.synthetic.main.fragment_bhaiya_home.*
import kotlinx.android.synthetic.main.layout_bottom_button.*
import org.jetbrains.annotations.NotNull
import java.util.*
import kotlin.collections.ArrayList


class BhaiyaHomeFragment : BaseFragment() {

    companion object {
        fun newInstance() = BhaiyaHomeFragment()
        var TAG = BhaiyaHomeFragment::class.java.simpleName
    }

    var appDisposable: AppDisposable = AppDisposable()
    private var houseId = -1
    private var user: User? = null
    val list: ArrayList<Dish> = ArrayList()
    var today = false
    var count = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(context).inflate(R.layout.fragment_bhaiya_home, null, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        EventsManager.setEventName(EventConstants.RESIDENT_SCREEN_VIEWED).send()
        houseId = SharedPreferenceManager.getHouseId()!!
        user = SharedPreferenceManager.getUser()
        getDishes(WeekType.TODAY.day)
        subscribeCommonTopic()

        nextBtn.text = ""//getString(R.string.change_plan)

        nextBtn.setOnClickListener {
            EventsManager.setEventName(EventConstants.CHANGE_PLAN_CLICKED)
                    .addProperty(BundleConstants.DAY, CommonUtil.getDay(count).toLowerCase())
                    .send()
            if (LocalDishManager.getTempDishList().size > 0) {
                LocalDishManager.clearTempDishList()
            }
            SharedPreferenceManager.setMyFoods(list)
            addFragment(ChangePlanFragment.newInstance(CommonUtil.getDay(count).toLowerCase()), ChangePlanFragment.TAG)
        }

        appDisposable.add(RxBus.listen(RxEvent.Action::class.java).subscribe { action ->
            if (isAdded) {
                when (action.eventType) {
                    RxEventType.REFRESH_DISH_LIST -> {
                        activity?.runOnUiThread {
                            Handler().postDelayed({
                                getDishes(CommonUtil.getDay(count).toLowerCase())
                                toggleYesterdayBtn()
                            }, 200)
                        }
                    }
                }
            }
        })

        yesterdayTv?.setOnClickListener {
            count -= 1
            changeDayEvent(count + 1)
            getDishes(CommonUtil.getDay(count).toLowerCase())
            toggleYesterdayBtn()
        }

        tomorrowTv?.setOnClickListener {
            count += 1
            changeDayEvent(count - 1)
            getDishes(CommonUtil.getDay(count).toLowerCase())
            toggleYesterdayBtn()
        }

        leftArrowsIv?.setOnClickListener {
            count -= 1
            changeDayEvent(count + 1)
            getDishes(CommonUtil.getDay(count).toLowerCase())
            toggleYesterdayBtn()
        }

        rightArrowsIv?.setOnClickListener {
            count += 1
            changeDayEvent(count - 1)
            getDishes(CommonUtil.getDay(count).toLowerCase())
            toggleYesterdayBtn()
        }

        toggleYesterdayBtn()

        rcv?.setOnTouchListener(object : OnSwipeTouchListener() {
            override fun onSwipeTop(): Boolean {
                return false
            }

            override fun onSwipeRight(): Boolean {
                if (CommonUtil.getDay(count).toLowerCase() != WeekType.TODAY.day) {
                    count -= 1
                    changeDayEvent(count + 1)
                    getDishes(CommonUtil.getDay(count).toLowerCase())
                    toggleYesterdayBtn()
                    return true
                }
                return false
            }

            override fun onSwipeLeft(): Boolean {
                count += 1
                changeDayEvent(count - 1)
                getDishes(CommonUtil.getDay(count).toLowerCase())
                toggleYesterdayBtn()
                return true
            }

            override fun onSwipeBottom(): Boolean {
                return false
            }
        })

        changeSideIv?.visibility = View.VISIBLE
        changeSideIv?.setOnClickListener {
            EventsManager.setEventName(EventConstants.SWITCH_USER_CLICKED)
                    .addProperty(BundleConstants.DAY, CommonUtil.getDay(count).toLowerCase())
                    .send()
            addFragment(PlanFragment.newInstance(count), PlanFragment.TAG)
        }
    }

    fun changeDayEvent(currentDayCount: Int) {
        EventsManager.setEventName(EventConstants.RESIDENT_DAY_CHANGE_SWIPED)
                .addProperty(BundleConstants.CURRENT_DAY, CommonUtil.getDay(currentDayCount).toLowerCase())
                .addProperty(BundleConstants.CHANGED_DAY, CommonUtil.getDay(count).toLowerCase())
                .send()
    }

    private fun toggleYesterdayBtn() {
        subtextTv.visibility = View.GONE
        rcv?.visibility = View.GONE
        zeroCaseLl?.visibility = View.GONE
        if (CommonUtil.getDay(count).toLowerCase() == WeekType.TODAY.day) {
            leftArrowsIv?.alpha = 0.3f
            yesterdayTv?.alpha = 0.4f

            leftArrowsIv?.isEnabled = false
            yesterdayTv?.isEnabled = false

            todayTv.text = getString(R.string.today)
            subtextTv.text = getString(R.string.you_ll_get_to_eat_these_dishes_tonight)
            zeroCaseTv.text = getString(R.string.empty_dish_list, getString(R.string.today))
        } else {
            leftArrowsIv?.alpha = 1f
            yesterdayTv?.alpha = 1f

            leftArrowsIv?.isEnabled = true
            yesterdayTv?.isEnabled = true

            todayTv.text = CommonUtil.getDay(count)
            val arg = CommonUtil.getDay(count, Locale.US)
            subtextTv.text = getString(R.string.you_ll_get_to_eat_these_dishes, arg.toLowerCase())
            zeroCaseTv.text = getString(R.string.empty_dish_list, arg)
        }
        val tempYesterDay = count - 1
        val tempTommrowDay = count + 1
        yesterdayTv?.text = CommonUtil.getShortDay(tempYesterDay, Locale.US)
        tomorrowTv?.text = CommonUtil.getShortDay(tempTommrowDay, Locale.US)
    }

    private fun getDishes(dayOfWeek: String) {
        progress?.visibility = View.VISIBLE
        nextBtn?.isEnabled = false
        nextBtnProgress?.visibility = View.VISIBLE
        nextBtn.text = ""

        val query = GetHouseUserDishesListQuery.builder()
                .dayOfWeek(dayOfWeek)
                .houseId(houseId)
                .languageId(user?.languageId!!)
                .userId(user?.id!!)
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
                        list.clear()
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

    fun setAdapter(list: ArrayList<Dish>?) {
        if (list != null) {
            SharedPreferenceManager.setMyFoods(list)
            val adapter = HouseDishesAdapter(context!!, UserType.RESIDENT, list) { any: Any, view: View, i: Int ->
                if (any is Dish) {
                    EventsManager.setEventName(EventConstants.RESIDENT_DISH_CLICKED)
                            .addProperty(BundleConstants.DAY, CommonUtil.getDay(count).toLowerCase())
                            .addProperty(BundleConstants.DISH_ID, any.id)
                            .addProperty(BundleConstants.DISH_NAME, any.dishName)
                            .send()
                }
            }
            rcv?.layoutManager = WrapContentGridLayoutManager(context!!, 3)
            if (rcv?.itemDecorationCount == 0) {
                rcv?.addItemDecoration(GridItemDecoration(context?.resources?.getDimensionPixelSize(R.dimen.dp_8)!!, 3))
            }
            progress?.visibility = View.GONE
            rcv?.visibility = View.VISIBLE
            nextBtnProgress?.visibility = View.GONE
            if (list.size > 0) {
                zeroCaseLl.visibility = View.GONE
                subtextTv.visibility = View.VISIBLE
                if (CommonUtil.getDay(count).toLowerCase() == WeekType.TODAY.day) {
                    nextBtn.text = getString(R.string.change_plan)
                } else {
                    val arg = CommonUtil.getDay(count, Locale.US)
                    nextBtn.text = getString(R.string.change_plan_for, arg)
                }
            } else {
                zeroCaseLl.visibility = View.VISIBLE
                subtextTv.visibility = View.GONE
                nextBtn.text = getString(R.string.add_dish)
            }
            rcv?.adapter = adapter
            nextBtn?.isEnabled = true
        }
    }

    private fun subscribeCommonTopic() {
        val commonTopic = SharedPreferenceManager.getHouseId().toString()//"all-users"
        FirebaseMessaging.getInstance().subscribeToTopic(commonTopic).addOnCompleteListener {
            if (it.isSuccessful){
                Log.d(TAG, "subscribeCommonTopic isSuccessful")
            } else {
                Log.d(TAG, "subscribeCommonTopic isFailed")
            }
        }.addOnFailureListener {
            Log.d(TAG, "subscribeCommonTopic ${it.stackTrace}")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        appDisposable.dispose()
    }
}
