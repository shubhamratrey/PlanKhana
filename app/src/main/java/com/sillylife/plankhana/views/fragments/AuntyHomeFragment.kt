package com.sillylife.plankhana.views.fragments

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
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
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.sillylife.plankhana.GetHouseDishesListQuery
import com.sillylife.plankhana.GetHouseResidentListQuery
import com.sillylife.plankhana.R
import com.sillylife.plankhana.enums.UserType
import com.sillylife.plankhana.enums.WeekType
import com.sillylife.plankhana.managers.sharedpreference.SharedPreferenceManager
import com.sillylife.plankhana.models.Dish
import com.sillylife.plankhana.models.User
import com.sillylife.plankhana.services.ApolloService
import com.sillylife.plankhana.services.AppDisposable
import com.sillylife.plankhana.utils.CommonUtil
import com.sillylife.plankhana.utils.OnSwipeTouchListener
import com.sillylife.plankhana.views.adapter.HouseDishesAdapter
import com.sillylife.plankhana.views.adapter.UserListAdapter
import com.sillylife.plankhana.views.adapter.item_decorator.GridItemDecoration
import com.sillylife.plankhana.views.adapter.item_decorator.WrapContentGridLayoutManager
import kotlinx.android.synthetic.main.bs_user_list.view.*
import kotlinx.android.synthetic.main.fragment_aunty_home.*
import org.jetbrains.annotations.NotNull
import java.util.*
import kotlin.collections.ArrayList

class AuntyHomeFragment : BaseFragment() {

    companion object {
        fun newInstance() = AuntyHomeFragment()
        var TAG = AuntyHomeFragment::class.java.simpleName
    }

    var appDisposable: AppDisposable = AppDisposable()
    val userList: ArrayList<User> = ArrayList()
    var houseId = -1
    var count = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(context).inflate(R.layout.fragment_aunty_home, null, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        houseId = SharedPreferenceManager.getHouseId()!!

        setHouseResidents()
        getHouseDishes(WeekType.TODAY.day)
        nextBtn?.setOnClickListener {
            showUserList(userList)
        }

        yesterdayTv?.setOnClickListener {
            count -= 1
            getHouseDishes(CommonUtil.getDay(count).toLowerCase())
            toggleYesterdayBtn()
        }

        tomorrowTv?.setOnClickListener {
            count += 1
            getHouseDishes(CommonUtil.getDay(count).toLowerCase())
            toggleYesterdayBtn()
        }

        leftArrowsIv?.setOnClickListener {
            count -= 1
            getHouseDishes(CommonUtil.getDay(count).toLowerCase())
            toggleYesterdayBtn()
        }

        rightArrowsIv?.setOnClickListener {
            count += 1
            getHouseDishes(CommonUtil.getDay(count).toLowerCase())
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
                    getHouseDishes(CommonUtil.getDay(count).toLowerCase())
                    toggleYesterdayBtn()
                    return true
                }
                return false
            }

            override fun onSwipeLeft(): Boolean {
                count += 1
                getHouseDishes(CommonUtil.getDay(count).toLowerCase())
                toggleYesterdayBtn()
                return true
            }

            override fun onSwipeBottom(): Boolean {
                return false
            }
        })
    }

    private fun toggleYesterdayBtn() {
        if (CommonUtil.getDay(count).toLowerCase() == WeekType.TODAY.day) {
            leftArrowsIv?.alpha = 0.3f
            yesterdayTv?.alpha = 0.4f

            leftArrowsIv?.isEnabled = false
            yesterdayTv?.isEnabled = false

            todayTv.text = getString(R.string.today)
        } else {
            leftArrowsIv?.alpha = 1f
            yesterdayTv?.alpha = 1f

            leftArrowsIv?.isEnabled = true
            yesterdayTv?.isEnabled = true

            todayTv.text = CommonUtil.getDay(count, Locale.getDefault())
        }

        val tempYesterDay = count - 1
        val tempTommrowDay = count + 1
        yesterdayTv?.text = CommonUtil.getShortDay(tempYesterDay, Locale.getDefault())
        tomorrowTv?.text = CommonUtil.getShortDay(tempTommrowDay, Locale.getDefault())
    }

    private fun getHouseDishes(dayOfWeek: String) {
        val user = SharedPreferenceManager.getUser()
        progress?.visibility = View.VISIBLE
        val list: ArrayList<Dish> = ArrayList()
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
                        for (dishes in response.data()?.plankhana_users_userdishweekplan_aggregate()?.nodes()?.toMutableList()!!) {
                            val userList: ArrayList<User> = ArrayList()
                            userList.clear()
                            for (users in dishes.dishes_dish().users_userdishweekplans().toMutableList()) {
                                userList.add(User(users.users_userprofile().id(), users.users_userprofile().username(), users.users_userprofile().display_picture()))
                            }
                            val name = if (dishes.dishes_dish().dishes_dishlanguagenames().size > 0) dishes.dishes_dish().dishes_dishlanguagenames()[0].dish_name() else ""
                            list.add(Dish(dishes.dishes_dish().id(), name, dishes.dishes_dish().dish_image(), userList))
                        }
                        activity?.runOnUiThread {
                            setAdapter(list)
                        }
                    }
                })
    }

    fun setAdapter(list: ArrayList<Dish>?) {
        if (list != null) {
            val adapter = HouseDishesAdapter(context!!, UserType.COOK, list) { any, pos ->

            }
            rcv?.layoutManager = WrapContentGridLayoutManager(context!!, 3)
            if (rcv?.itemDecorationCount == 0) {
                rcv?.addItemDecoration(GridItemDecoration(context?.resources?.getDimensionPixelSize(R.dimen.dp_8)!!, 3))
            }
            progress?.visibility = View.GONE
            rcv?.adapter = adapter
        }
    }

    private fun setHouseResidents() {
        val query = GetHouseResidentListQuery.builder()
                .houseId(houseId)
                .userType(UserType.RESIDENT.type)
                .build()
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
                        for (user in response.data()?.plankhana_houses_houseuser()?.toMutableList()!!) {
                            userList.add(User(user.users_userprofile().id(), user.users_userprofile().username(), user.users_userprofile().display_picture(), user.users_userprofile().phone()))
                        }
                    }
                })
    }

    private fun showUserList(users: ArrayList<User>) {
        val bottomSheet = BottomSheetDialog(context!!, R.style.BottomSheetDialog)
        val sheetView = layoutInflater.inflate(R.layout.bs_user_list, null)

        val adapter = UserListAdapter(context!!, users) {
            if (it is User) {
                callUs(it.phone!!)
            }
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

    private fun callUs(phoneNumber: String) {
        Dexter.withActivity(activity).withPermission(Manifest.permission.CALL_PHONE)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse) {
                        try {
                            val callIntent = Intent(Intent.ACTION_CALL)
                            callIntent.data = Uri.parse("tel:$phoneNumber")
                            startActivity(callIntent)
                        } catch (activityException: ActivityNotFoundException) {
                            activityException.printStackTrace()
                        }

                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse) {
                        showPermissionRequiredDialog(getString(R.string.call_permission_message))
                    }

                    override fun onPermissionRationaleShouldBeShown(
                            permission: PermissionRequest, token: PermissionToken) {
                        token.continuePermissionRequest()
                    }
                }).check()
    }


    override fun onDestroy() {
        super.onDestroy()
        appDisposable.dispose()
    }
}
