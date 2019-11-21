package com.sillylife.plankhana.views.fragments

import android.content.Intent
import android.graphics.Color
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
import com.google.android.material.snackbar.Snackbar
import com.sillylife.plankhana.GetAllDishesQuery
import com.sillylife.plankhana.GetDishesQuery
import com.sillylife.plankhana.R
import com.sillylife.plankhana.managers.LocalDishManager
import com.sillylife.plankhana.managers.sharedpreference.SharedPreferenceManager
import com.sillylife.plankhana.models.Dish
import com.sillylife.plankhana.models.DishCategory
import com.sillylife.plankhana.models.DishStatus
import com.sillylife.plankhana.models.User
import com.sillylife.plankhana.services.ApolloService
import com.sillylife.plankhana.services.AppDisposable
import com.sillylife.plankhana.utils.CommonUtil
import com.sillylife.plankhana.utils.rxevents.RxBus
import com.sillylife.plankhana.utils.rxevents.RxEvent
import com.sillylife.plankhana.utils.rxevents.RxEventType
import com.sillylife.plankhana.views.activities.WebActivity
import com.sillylife.plankhana.views.adapter.DishesAdapter
import com.sillylife.plankhana.views.adapter.item_decorator.ItemDecorator
import kotlinx.android.synthetic.main.fragment_add_dish_child.*
import org.jetbrains.annotations.NotNull

class AddDishChildFragment : BaseFragment() {

    companion object {
        fun newInstance(): AddDishChildFragment {
            return AddDishChildFragment()
        }

        fun newInstance(type: String): AddDishChildFragment {
            val fragment = AddDishChildFragment()
            val args = Bundle()
            args.putString("type", type)
            fragment.arguments = args
            return fragment
        }


        fun newInstance(dishCategory: DishCategory): AddDishChildFragment {
            val fragment = AddDishChildFragment()
            val args = Bundle()
            args.putParcelable("dishCategory", dishCategory)
            fragment.arguments = args
            return fragment
        }

        val TAG: String = AddDishChildFragment::class.java.simpleName
    }

    private var mDishCategory: DishCategory? = null
    private var user: User? = null
    var appDisposable: AppDisposable = AppDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_add_dish_child, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        user = SharedPreferenceManager.getUser()

        val dishIds = LocalDishManager.getSavedDishesIds()
        val tempDishIds = LocalDishManager.getTempSavedDishesIds()

        if (arguments != null && arguments!!.containsKey("dishCategory")) {
            mDishCategory = arguments?.getParcelable("dishCategory")
            getDishes(mDishCategory?.id!!)
        } else if (arguments != null && arguments!!.containsKey("type") && arguments?.getString("type")?.contains("all")!!) {
            getAllDishes()
        } else {
            val list: ArrayList<Dish> = ArrayList()
            LocalDishManager.getFavouriteDishes().forEach { dishes ->
                when {
                    tempDishIds.contains(dishes.id) -> list.add(
                        Dish(
                            id = dishes.id,
                            dishName = dishes.dishName,
                            dishImage = dishes.dishImage,
                            dishStatus = DishStatus(added = true)
                        )
                    )
                    dishIds.contains(dishes.id) -> list.add(
                        Dish(
                            id = dishes.id,
                            dishName = dishes.dishName,
                            dishImage = dishes.dishImage,
                            dishStatus = DishStatus(alreadyAdded = true)
                        )
                    )
                    else -> list.add(
                        Dish(
                            id = dishes.id,
                            dishName = dishes.dishName,
                            dishImage = dishes.dishImage,
                            dishStatus = DishStatus(add = true)
                        )
                    )
                }
            }
            setAdapter(list)
        }

        appDisposable.add(RxBus.listen(RxEvent.Action::class.java).subscribe { action ->
            if (isAdded) {
                when (action.eventType) {
                    RxEventType.DISH_ADDED_REMOVED -> {
                        val dish = action.items[0] as Dish?
                        if (rcv.adapter != null && dish != null) {
                            val adapter = rcv.adapter as DishesAdapter
                            adapter.changeDishStatus(dish)
                        }
                    }
                }
            }
        })
    }

    private fun getAllDishes() {
        progress?.visibility = View.VISIBLE
        val list: ArrayList<Dish> = ArrayList()
        val query = GetAllDishesQuery.builder()
                .limit(1200)
                .offset(0)
                .languageId(user?.languageId!!)
                .build()

        val dishIds = LocalDishManager.getSavedDishesIds()
        val tempDishIds = LocalDishManager.getTempSavedDishesIds()
        ApolloService.buildApollo().query(query)
                .responseFetcher(ApolloResponseFetchers.NETWORK_ONLY)
                .enqueue(object : ApolloCall.Callback<GetAllDishesQuery.Data>() {
                    override fun onFailure(error: ApolloException) {
                        Log.d(SelectBhaiyaFragment.TAG, error.toString())
                    }

                    override fun onResponse(@NotNull response: Response<GetAllDishesQuery.Data>) {
                        if (!isAdded) {
                            return
                        }
                        Log.d(TAG, "STARTED")
                        for (dishes in response.data()?.plankhana_dishes_dish()?.toMutableList()!!) {
                            val name = if (dishes.dishes_dishlanguagenames().size > 0) dishes.dishes_dishlanguagenames()[0].dish_name() else ""
                            when {
                                tempDishIds.contains(dishes.id()) -> list.add(
                                    Dish(
                                        id = dishes.id(),
                                        dishName = name,
                                        dishImage = dishes.dish_image(),
                                        dishStatus = DishStatus(added = true)
                                    )
                                )
                                dishIds.contains(dishes.id()) -> list.add(
                                    Dish(
                                        id = dishes.id(),
                                        dishName = name,
                                        dishImage = dishes.dish_image(),
                                        dishStatus = DishStatus(alreadyAdded = true)
                                    )
                                )
                                else -> list.add(
                                    Dish(
                                        id = dishes.id(),
                                        dishName = name,
                                        dishImage = dishes.dish_image(),
                                        dishStatus = DishStatus(add = true)
                                    )
                                )
                            }
                        }
                        activity?.runOnUiThread {
                            Log.d(TAG, "ENDED")
                            setAdapter(list)
                        }
                    }
                })
    }

    private fun getDishes(categoryId: Int) {
        progress?.visibility = View.VISIBLE
        val list: ArrayList<Dish> = ArrayList()
        val query = GetDishesQuery.builder()
                .categoryId(categoryId)
                .limit(1200)
                .offset(0)
                .languageId(user?.languageId!!)
                .build()

        val dishIds = LocalDishManager.getSavedDishesIds()
        val tempDishIds = LocalDishManager.getTempSavedDishesIds()
        ApolloService.buildApollo().query(query)
                .responseFetcher(ApolloResponseFetchers.NETWORK_ONLY)
                .enqueue(object : ApolloCall.Callback<GetDishesQuery.Data>() {
                    override fun onFailure(error: ApolloException) {
                        Log.d(SelectBhaiyaFragment.TAG, error.toString())
                    }

                    override fun onResponse(@NotNull response: Response<GetDishesQuery.Data>) {
                        if (!isAdded) {
                            return
                        }
                        for (dishes in response.data()?.plankhana_dishes_dish()?.toMutableList()!!) {
                            val name = if (dishes.dishes_dishlanguagenames().size > 0) dishes.dishes_dishlanguagenames()[0].dish_name() else ""
                            when {
                                tempDishIds.contains(dishes.id()) -> list.add(
                                    Dish(
                                        id = dishes.id(),
                                        dishName = name,
                                        dishImage = dishes.dish_image(),
                                        same_day_available = dishes.same_day_available(),
                                        dishStatus = DishStatus(added = true)
                                    )
                                )
                                dishIds.contains(dishes.id()) -> list.add(
                                    Dish(
                                        id = dishes.id(),
                                        dishName = name,
                                        dishImage = dishes.dish_image(),
                                        same_day_available = dishes.same_day_available(),
                                        dishStatus = DishStatus(alreadyAdded = true)
                                    )
                                )
                                else -> list.add(
                                    Dish(
                                        id = dishes.id(),
                                        dishName = name,
                                        dishImage = dishes.dish_image(),
                                        same_day_available = dishes.same_day_available(),
                                        dishStatus = DishStatus(add = true)
                                    )
                                )
                            }
                        }
                        activity?.runOnUiThread {
                            setAdapter(list)
                        }
                    }
                })
    }

    fun setAdapter(list: ArrayList<Dish>?) {
        if (list != null) {
            val adapter = DishesAdapter(context!!, DishesAdapter.REQUEST_A_DISH, list) { any, type, pos ->
                if (any is String) {
                    if (any.contentEquals(DishesAdapter.Add_A_DISH)) {
                        val intent = Intent(activity, WebActivity::class.java)
                        startActivity(intent)
                    } else {
                        val snackBar = Snackbar.make(rcv, "You can not add this today", Snackbar.LENGTH_LONG)
                        snackBar.anchorView = snackbar_action
                        snackBar.setBackgroundTint(Color.parseColor("#CE3044"))
                        snackBar.setTextColor(Color.parseColor("#FFFFFFFF"))
                        snackBar.show()
                    }
                } else if (any is Dish) {
                    RxBus.publish(RxEvent.Action(RxEventType.DISH_ADDED_REMOVED, any))
                    if (type.contains(DishesAdapter.ADD)) {
                        RxBus.publish(RxEvent.Action(RxEventType.CHANGE_PLAN_LIST_DISH_ADD, any))
                        LocalDishManager.addTempDish(any)
                    } else if (type.contains(DishesAdapter.REMOVE)) {
                        RxBus.publish(RxEvent.Action(RxEventType.CHANGE_PLAN_LIST_DISH_REMOVE, any))
                        LocalDishManager.removeTempDish(any)
                    }

                    //toggleBtn
                    if (parentFragment != null && parentFragment is AddDishFragment) {
                        (parentFragment as AddDishFragment).toggleBtn()
                    }
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
        appDisposable.dispose()
    }
}
