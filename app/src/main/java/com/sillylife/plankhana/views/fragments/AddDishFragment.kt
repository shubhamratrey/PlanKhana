package com.sillylife.plankhana.views.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.viewpager.widget.ViewPager
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.fetcher.ApolloResponseFetchers
import com.sillylife.plankhana.GetDishCategoriesQuery
import com.sillylife.plankhana.R
import com.sillylife.plankhana.models.DishCategory
import com.sillylife.plankhana.services.ApolloService
import com.sillylife.plankhana.services.AppDisposable
import com.sillylife.plankhana.utils.CommonUtil
import com.sillylife.plankhana.views.adapter.CommonViewStatePagerAdapter
import kotlinx.android.synthetic.main.fragment_add_dish.*
import kotlinx.android.synthetic.main.fragment_change_plan.*
import org.jetbrains.annotations.NotNull
import java.io.File

class AddDishFragment : BaseFragment() {

    companion object {
        fun newInstance(): AddDishFragment {
            return AddDishFragment()
        }

        val TAG: String = AddDishFragment::class.java.simpleName
    }


    private var rootView: View? = null
    lateinit var viewPagerAdapter: CommonViewStatePagerAdapter
    private var appDisposable: AppDisposable = AppDisposable()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        rootView = inflater.inflate(R.layout.fragment_add_dish, container, false)
        return rootView!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeSearch()

        backArrowFl.setOnClickListener {
            fragmentManager?.popBackStack()
        }
        setTabs()
    }

    private fun setTabs() {
        progress?.visibility = View.VISIBLE
        val list: ArrayList<DishCategory> = ArrayList()
        val query = GetDishCategoriesQuery.builder().build()

        ApolloService.buildApollo().query(query)
                .responseFetcher(ApolloResponseFetchers.NETWORK_ONLY)
                .enqueue(object : ApolloCall.Callback<GetDishCategoriesQuery.Data>() {
                    override fun onFailure(error: ApolloException) {
                        Log.d(SelectBhaiyaFragment.TAG, error.toString())
                    }

                    override fun onResponse(@NotNull response: Response<GetDishCategoriesQuery.Data>) {
                        if (!isAdded) {
                            return
                        }
                        for (ca in response.data()?.plankhana_dishes_category()?.toMutableList()!!) {
                            list.add(DishCategory(ca.id(), ca.category()))
                        }
                        activity?.runOnUiThread {
                            updateTabs(list)
                        }
                    }
                })
    }

    private fun updateTabs(category: ArrayList<DishCategory>) {

        tabs.visibility = View.VISIBLE
        profileViewPager.visibility = View.VISIBLE

        viewPagerAdapter = CommonViewStatePagerAdapter(childFragmentManager)


        val allGenres = ArrayList<AddDishChildFragment>()

        for (b in category) {
            val genreFragment = AddDishChildFragment.newInstance(b)
            allGenres.add(genreFragment)
        }

        val favorite = AddDishChildFragment.newInstance()
        viewPagerAdapter.addItem(favorite, "Favorites")

        val all = AddDishChildFragment.newInstance("all")
        viewPagerAdapter.addItem(all, "All")

        for (b in category) {
            val fragment = AddDishChildFragment.newInstance(b)
            viewPagerAdapter.addItem(fragment, b.category!!)
        }

        profileViewPager.adapter = viewPagerAdapter
        tabs.setupWithViewPager(profileViewPager)
        profileViewPager.offscreenPageLimit = 2
        setSwipeListenerOnViewPager()

        preLoader.visibility = View.GONE
    }

    private fun setSwipeListenerOnViewPager() {
        profileViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {

            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })
    }

    private fun initializeSearch() {
        searchIconIv.setOnClickListener {

            toggleAudioSearch()
            searchView.requestFocus()
            CommonUtil.showKeyboard(context)
        }
        searchView.isActivated = false
        searchView.queryHint = ""//getString(R.string.type_your_filename_here)
        searchView.onActionViewExpanded()
        searchView.isIconified = false
        searchView.clearFocus()
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
//                if (!TextUtils.isEmpty(newText)) {
//                    var f = viewPagerAdapter.getFragment(profileViewPager.currentItem) as AddDishChildFragment
//                    var adapter = f.getAdapter()
//                    if (adapter != null && adapter?.filter != null) {
//                        adapter?.filter?.filter(newText)
//                    }
//
//                }
                return false
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
//                if (!TextUtils.isEmpty(query)) {
//                    val f = viewPagerAdapter.getFragment(profileViewPager.currentItem) as AddDishChildFragment
//                    val adapter = f.getAdapter()
//
//                    if (adapter != null && adapter?.filter != null) {
//                        adapter?.filter?.filter(query)
//                    }
//                }
                return false
            }
        })

        val llSubmitArea: LinearLayout = searchView.findViewById(androidx.appcompat.R.id.submit_area)
        llSubmitArea.background = null

        val closeButton = searchView.findViewById(R.id.search_close_btn) as ImageView
        closeButton.setOnClickListener {
            //            toggleAudioSearch()
//            searchView.setQuery("", false)
//            val f = viewPagerAdapter.getFragment(profileViewPager.currentItem) as AddDishChildFragment
//            val adapter = f.getAdapter()
//
//            if (adapter != null && adapter?.filter != null) {
//                adapter?.filter?.filter("")
//            }
        }

//        searchView.setOnQueryTextFocusChangeListener { view, hasFocus ->
//            if (!hasFocus) {
//                toggleAudioSearch()
//                searchView.setQuery("", false)
//                adapter!!.filter.filter("")
//            }
//        }
    }

    private fun toggleAudioSearch() {
        if (searchIconIv.isVisible) {
            searchView.visibility = View.VISIBLE
            toolbarTitleTv.visibility = View.GONE
            searchIconIv.visibility = View.GONE
        } else {
            searchView.visibility = View.GONE
            toolbarTitleTv.visibility = View.VISIBLE
            searchIconIv.visibility = View.VISIBLE
        }
    }

    override fun onDetach() {
        super.onDetach()
        appDisposable.dispose()
    }


    override fun onDestroy() {
        super.onDestroy()
        appDisposable.dispose()
    }


}
