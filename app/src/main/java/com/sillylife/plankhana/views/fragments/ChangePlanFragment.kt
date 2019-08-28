package com.sillylife.plankhana.views.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.fetcher.ApolloResponseFetchers
import com.sillylife.plankhana.GetHouseUserDishesListQuery
import com.sillylife.plankhana.R
import com.sillylife.plankhana.managers.FoodManager
import com.sillylife.plankhana.managers.sharedpreference.SharedPreferenceManager
import com.sillylife.plankhana.models.Dish
import com.sillylife.plankhana.models.User
import com.sillylife.plankhana.services.ApolloService
import com.sillylife.plankhana.services.AppDisposable
import com.sillylife.plankhana.utils.CommonUtil
import com.sillylife.plankhana.views.adapter.DishesAdapter
import com.sillylife.plankhana.views.adapter.item_decorator.DividerItemDecorator
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
//        getDishes(user?.id!!)
        nextBtn.text = getString(R.string.string_continue)

        nextBtn.setOnClickListener {

        }

        closeBtn.setOnClickListener {
            fragmentManager?.popBackStack()
        }

        setAdapter(FoodManager.getMyFood())
    }

    private fun getDishes(userId: Int) {
        progress?.visibility = View.VISIBLE
        val list: ArrayList<Dish> = ArrayList()
        val query = GetHouseUserDishesListQuery.builder()
//                .dayOfWeek(WeekType.TODAY.day)
                .dayOfWeek("monday")
                .houseId(houseId)
                .userId(userId)
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
                            list.add(Dish(dishes.dishes_dish().id(), dishes.dishes_dish().dish_name(), dishes.dishes_dish().dish_image()))
                        }
                        activity?.runOnUiThread {
                            setAdapter(list)
                        }
                    }
                })
    }

    fun setAdapter(list: ArrayList<Dish>?) {
        if (list != null) {
            val adapter = DishesAdapter(context!!, list) { any, pos ->

            }
            rcv?.layoutManager = LinearLayoutManager(context!!)
            if (rcv?.itemDecorationCount == 0){
                rcv?.addItemDecoration(DividerItemDecorator(ContextCompat.getDrawable(context!!, R.drawable.d_line_separator),
                        CommonUtil.dpToPx(40),
                        CommonUtil.dpToPx(20),
                        CommonUtil.dpToPx(20),
                        CommonUtil.dpToPx(20),
                        CommonUtil.dpToPx(20)))
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
