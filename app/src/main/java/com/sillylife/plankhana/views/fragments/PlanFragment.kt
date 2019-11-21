package com.sillylife.plankhana.views.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.fetcher.ApolloResponseFetchers
import com.sillylife.plankhana.GetHouseDishesListQuery
import com.sillylife.plankhana.R
import com.sillylife.plankhana.constants.EventConstants
import com.sillylife.plankhana.enums.UserType
import com.sillylife.plankhana.enums.WeekType
import com.sillylife.plankhana.managers.EventsManager
import com.sillylife.plankhana.managers.sharedpreference.SharedPreferenceManager
import com.sillylife.plankhana.models.Dish
import com.sillylife.plankhana.models.User
import com.sillylife.plankhana.services.ApolloService
import com.sillylife.plankhana.services.AppDisposable
import com.sillylife.plankhana.utils.CommonUtil
import com.sillylife.plankhana.utils.ImageManager
import com.sillylife.plankhana.utils.OnSwipeTouchListener
import com.sillylife.plankhana.views.adapter.HouseDishesAdapter
import com.sillylife.plankhana.views.adapter.item_decorator.GridItemDecoration
import com.sillylife.plankhana.views.adapter.item_decorator.WrapContentGridLayoutManager
import kotlinx.android.synthetic.main.fragment_plan.*
import org.jetbrains.annotations.NotNull
import java.util.*
import kotlin.collections.ArrayList


class PlanFragment : BaseFragment() {

    companion object {
        fun newInstance() = PlanFragment()
        fun newInstance(count: Int): PlanFragment {
            val fragment = PlanFragment()
            val args = Bundle()
            args.putInt("count", count)
            fragment.arguments = args
            return fragment
        }

        var TAG = PlanFragment::class.java.simpleName
    }

    var appDisposable: AppDisposable = AppDisposable()
    private var houseId = -1
    private var user: User? = null
    val list: ArrayList<Dish> = ArrayList()
    var today = false
    var count = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(context).inflate(R.layout.fragment_plan, null, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        EventsManager.setEventName(EventConstants.HOUSE_PLAN_SCREEN_VIEWED).send()
        if (arguments != null && arguments!!.containsKey("count")) {
            count = arguments?.getInt("count")!!
        }

        houseId = SharedPreferenceManager.getHouseId()!!
        user = SharedPreferenceManager.getUser()
        getDishes(if (count != 0) CommonUtil.getDay(count).toLowerCase() else WeekType.TODAY.day)
        backArrowFl.setOnClickListener {
            fragmentManager?.popBackStack()
        }

        yesterdayTv?.setOnClickListener {
            count -= 1
            getDishes(CommonUtil.getDay(count).toLowerCase())
            toggleYesterdayBtn()
        }

        tomorrowTv?.setOnClickListener {
            count += 1
            getDishes(CommonUtil.getDay(count).toLowerCase())
            toggleYesterdayBtn()
        }

        leftArrowsIv?.setOnClickListener {
            count -= 1
            getDishes(CommonUtil.getDay(count).toLowerCase())
            toggleYesterdayBtn()
        }

        rightArrowsIv?.setOnClickListener {
            count += 1
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
                    getDishes(CommonUtil.getDay(count).toLowerCase())
                    toggleYesterdayBtn()
                    return true
                }
                return false
            }

            override fun onSwipeLeft(): Boolean {
                count += 1
                getDishes(CommonUtil.getDay(count).toLowerCase())
                toggleYesterdayBtn()
                return true
            }

            override fun onSwipeBottom(): Boolean {
                return false
            }
        })
        if (!CommonUtil.textIsEmpty(user?.imageUrl)) {
            ImageManager.loadImageCircular(userImageIv, user?.imageUrl)
        }
        userImageFl?.setOnClickListener {
            EventsManager.setEventName(EventConstants.HOUSE_PLAN_RESIDENT_PHOTO_CLICKED).send()
        }
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
            subtextTv.text = getString(R.string.you_all_get_to_eat_these_dishes_tonight)
            zeroCaseTv.text = getString(R.string.empty_dish_list, getString(R.string.today))
        } else {
            leftArrowsIv?.alpha = 1f
            yesterdayTv?.alpha = 1f

            leftArrowsIv?.isEnabled = true
            yesterdayTv?.isEnabled = true

            todayTv.text = CommonUtil.getDay(count)
            val arg = CommonUtil.getDay(count, Locale.US)
            subtextTv.text = getString(R.string.you_all_get_to_eat_these_dishes, arg.toLowerCase())
            zeroCaseTv.text = getString(R.string.empty_dish_list, arg)
        }
        val tempYesterDay = count - 1
        val tempTommrowDay = count + 1
        yesterdayTv?.text = CommonUtil.getShortDay(tempYesterDay, Locale.US)
        tomorrowTv?.text = CommonUtil.getShortDay(tempTommrowDay, Locale.US)
    }

    private fun getDishes(dayOfWeek: String) {
        progress?.visibility = View.VISIBLE
        val query = GetHouseDishesListQuery.builder()
                .dayOfWeek(dayOfWeek)
                .languageId(user?.languageId!!)
                .houseId(houseId)
                .build()

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
                        list.clear()
                        for (dishes in response.data()?.plankhana_users_userdishweekplan_aggregate()?.nodes()?.toMutableList()!!) {
                            val userList: ArrayList<User> = ArrayList()
                            userList.clear()
                            for (users in dishes.dishes_dish().users_userdishweekplans().toMutableList()) {
                                userList.add(User(users.users_userprofile().id(), users.users_userprofile().username(), users.users_userprofile().display_picture()))
                            }
                            val name = if (dishes.dishes_dish().dishes_dishlanguagenames().size > 0) dishes.dishes_dish().dishes_dishlanguagenames()[0].dish_name() else ""
                            list.add(
                                Dish(
                                    id = dishes.dishes_dish().id(),
                                    dishName = name,
                                    dishImage = dishes.dishes_dish().dish_image(),
                                    userList = userList
                                )
                            )
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
            val adapter = HouseDishesAdapter(context!!, UserType.COOK, list) { any: Any, view: View, i: Int ->

            }
            rcv?.layoutManager = WrapContentGridLayoutManager(context!!, 3)
            if (rcv?.itemDecorationCount == 0) {
                rcv?.addItemDecoration(GridItemDecoration(context?.resources?.getDimensionPixelSize(R.dimen.dp_8)!!, 3))
            }
            progress?.visibility = View.GONE
            rcv?.visibility = View.VISIBLE
            if (list.size > 0) {
                zeroCaseLl.visibility = View.GONE
                subtextTv.visibility = View.VISIBLE
            } else {
                zeroCaseLl.visibility = View.VISIBLE
                subtextTv.visibility = View.GONE
            }
            rcv?.adapter = adapter
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        appDisposable.dispose()
    }
}
