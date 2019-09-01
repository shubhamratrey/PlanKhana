package com.sillylife.plankhana.views.fragments

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Point
import android.graphics.Rect
import android.graphics.RectF
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
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
import com.sillylife.plankhana.utils.ImageManager
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

    private var mCurrentAnimator: Animator? = null
    private var startBounds: RectF? = null
    private var finalBounds: RectF? = null
    private var startScale: Float? = null
    private var isImageExpanded: Boolean = false
    private var mShortAnimationDuration: Int = 250
    private var thumbView:View? = null

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

        expandedCloseBtn?.setOnClickListener {
            closeExpandedImage(startBounds!!, startScale!!, thumbView!!)
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
            subtextTv.text = getString(R.string.please_cook_these_dishes_for_dinner_tonight)
            zeroCaseTv.text = getString(R.string.aunty_empty_dish_list, getString(R.string.today))
        } else {
            leftArrowsIv?.alpha = 1f
            yesterdayTv?.alpha = 1f

            leftArrowsIv?.isEnabled = true
            yesterdayTv?.isEnabled = true

            todayTv.text = CommonUtil.getDay(count, Locale.getDefault())
            val arg = CommonUtil.getDay(count, Locale.getDefault())
            subtextTv.text = getString(R.string.please_cook_these_dishes_on, arg.toLowerCase())
            zeroCaseTv.text = getString(R.string.aunty_empty_dish_list, arg)
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
            val adapter = HouseDishesAdapter(context!!, UserType.COOK, list) { any: Any, view: View, i: Int ->
                if (any is Dish){
                    thumbView = view
                    zoomImageFromThumb(view, any)
                }
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

    private fun zoomImageFromThumb(thumbView: View, dish:Dish) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        mCurrentAnimator?.cancel()

        // Load the high-resolution "zoomed-in" image.

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        val startBoundsInt = Rect()
        val finalBoundsInt = Rect()
        val globalOffset = Point()

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(startBoundsInt)
        parent.getGlobalVisibleRect(finalBoundsInt, globalOffset)
        startBoundsInt.offset(-globalOffset.x, -globalOffset.y)
        finalBoundsInt.offset(-globalOffset.x, -globalOffset.y)

        startBounds = RectF(startBoundsInt)
        finalBounds = RectF(finalBoundsInt)

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        if ((finalBounds!!.width() / finalBounds!!.height() > startBounds!!.width() / startBounds!!.height())) {
            // Extend start bounds horizontally
            startScale = startBounds!!.height() / finalBounds!!.height()
            val startWidth: Float = startScale!! * finalBounds!!.width()
            val deltaWidth: Float = (startWidth - startBounds!!.width()) / 2
            startBounds!!.left -= deltaWidth.toInt()
            startBounds!!.right += deltaWidth.toInt()
        } else {
            // Extend start bounds vertically
            startScale = startBounds!!.width() / finalBounds!!.width()
            val startHeight: Float = startScale!! * finalBounds!!.height()
            val deltaHeight: Float = (startHeight - startBounds!!.height()) / 2f
            startBounds!!.top -= deltaHeight.toInt()
            startBounds!!.bottom += deltaHeight.toInt()
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        thumbView.alpha = 0f
        rlExpandedImage?.visibility = View.VISIBLE
        if (dish != null && !CommonUtil.textIsEmpty(dish.dishImage)) {
            ImageManager.loadImage(expanded_image!!, dish.dishImage)
        }

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        rlExpandedImage?.pivotX = 0f
        rlExpandedImage?.pivotY = 0f

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        mCurrentAnimator = AnimatorSet().apply { play(ObjectAnimator.ofFloat(rlExpandedImage!!, View.X, startBounds!!.left, finalBounds!!.left)).apply {
                with(ObjectAnimator.ofFloat(rlExpandedImage!!, View.Y, startBounds!!.top, finalBounds!!.top))
                with(ObjectAnimator.ofFloat(rlExpandedImage!!, View.SCALE_X, startScale!!, 1f))
                with(ObjectAnimator.ofFloat(rlExpandedImage!!, View.SCALE_Y, startScale!!, 1f))
            }
            duration = mShortAnimationDuration.toLong()
            interpolator = DecelerateInterpolator()
            addListener(object : AnimatorListenerAdapter() {

                override fun onAnimationEnd(animation: Animator) {
                    mCurrentAnimator = null
                    rlExpandedImage.setBackgroundColor(ContextCompat.getColor(context!!, R.color.black_alpha30))
                }

                override fun onAnimationCancel(animation: Animator) {
                    mCurrentAnimator = null
                    rlExpandedImage.setBackgroundColor(ContextCompat.getColor(context!!, android.R.color.transparent))
                }
            })
            start()
        }

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        rlExpandedImage?.setOnClickListener {
            closeExpandedImage(startBounds!!, startScale!!, thumbView)
        }
        isImageExpanded = true
    }

    private fun closeExpandedImage(startBounds: RectF, startScale: Float, thumbView: View) {
        rlExpandedImage?.setBackgroundColor(ContextCompat.getColor(context!!, android.R.color.transparent))
        mCurrentAnimator?.cancel()

        // Animate the four positioning/sizing properties in parallel,
        // back to their original values.
        mCurrentAnimator = AnimatorSet().apply {
            play(ObjectAnimator.ofFloat(rlExpandedImage!!, View.X, startBounds.left)).apply {
                with(ObjectAnimator.ofFloat(rlExpandedImage!!, View.Y, startBounds.top))
                with(ObjectAnimator.ofFloat(rlExpandedImage!!, View.SCALE_X, startScale))
                with(ObjectAnimator.ofFloat(rlExpandedImage!!, View.SCALE_Y, startScale))
            }
            duration = mShortAnimationDuration.toLong()
            interpolator = DecelerateInterpolator()
            addListener(object : AnimatorListenerAdapter() {

                override fun onAnimationEnd(animation: Animator) {
                    thumbView.alpha = 1f
                    rlExpandedImage?.visibility = View.GONE
                    mCurrentAnimator = null
                }

                override fun onAnimationCancel(animation: Animator) {
                    thumbView.alpha = 1f
                    rlExpandedImage?.visibility = View.GONE
                    mCurrentAnimator = null
                }
            })
            start()
        }
        isImageExpanded = false
    }

    fun closeExpandedImage(): Boolean {
        if (isImageExpanded) {
            isImageExpanded = false
            closeExpandedImage(startBounds!!, startScale!!, thumbView!!)
            return false
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        appDisposable.dispose()
    }
}
