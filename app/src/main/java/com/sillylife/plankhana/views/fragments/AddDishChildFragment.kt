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
import com.sillylife.plankhana.GetAllDishesQuery
import com.sillylife.plankhana.GetDishesQuery
import com.sillylife.plankhana.R
import com.sillylife.plankhana.managers.LocalDishManager
import com.sillylife.plankhana.models.Dish
import com.sillylife.plankhana.models.DishCategory
import com.sillylife.plankhana.models.DishStatus
import com.sillylife.plankhana.services.ApolloService
import com.sillylife.plankhana.utils.CommonUtil
import com.sillylife.plankhana.utils.rxevents.RxBus
import com.sillylife.plankhana.utils.rxevents.RxEvent
import com.sillylife.plankhana.utils.rxevents.RxEventType
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
    private var FORM_LINK: String = "https://docs.google.com/forms/d/e/1FAIpQLSfbc_HduyBxmGnfpc4zrPGVMFRGIeAVHvZf8mPzuAtSc6KUOg/viewform?usp=sf_link"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_add_dish_child, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null && arguments!!.containsKey("dishCategory")) {
            mDishCategory = arguments?.getParcelable("dishCategory")
            getDishes(mDishCategory?.id!!)
        } else if (arguments != null && arguments!!.containsKey("type") && arguments?.getString("type")?.contains("all")!!) {
            getAllDishes()
        } else {
            val temp: ArrayList<Dish> = ArrayList()
            LocalDishManager.getFavouriteDishes().forEach {
               temp.add(Dish(it.id, it.dishName, it.dishImage))
            }
            setAdapter(temp)
        }
    }

    private fun getAllDishes() {
        progress?.visibility = View.VISIBLE
        val list: ArrayList<Dish> = ArrayList()
        val query = GetAllDishesQuery.builder()
                .limit(1200)
                .offset(0)
                .build()

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
                        for (dishes in response.data()?.plankhana_dishes_dish()?.toMutableList()!!) {
                            list.add(Dish(dishes.id(), dishes.dish_name(), dishes.dish_image(), DishStatus(add = true)))
                        }
                        activity?.runOnUiThread {
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
                .build()

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
                            list.add(Dish(dishes.id(), dishes.dish_name(), dishes.dish_image(), DishStatus(add = true)))
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
                if (any is String && any.contentEquals(DishesAdapter.Add_A_DISH)) {
                    addFragment(WebViewFragment.newInstance(FORM_LINK), WebViewFragment.TAG)
                }
            }
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
    }
}
