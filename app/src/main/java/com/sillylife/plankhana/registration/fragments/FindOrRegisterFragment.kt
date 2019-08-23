package com.sillylife.plankhana.registration.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.fetcher.ApolloResponseFetchers
import com.sillylife.plankhana.AddHouseKeyMutation
import com.sillylife.plankhana.GetAllHouseIdsQuery
import com.sillylife.plankhana.R
import com.sillylife.plankhana.SearchHouseKeyQuery
import com.sillylife.plankhana.services.ApolloService
import com.sillylife.plankhana.services.AppDisposable
import com.sillylife.plankhana.views.BaseFragment
import kotlinx.android.synthetic.main.fragment_find_or_register.*
import org.jetbrains.annotations.NotNull

class FindOrRegisterFragment : BaseFragment() {

    companion object {
        var TAG = FindOrRegisterFragment::class.java.simpleName
        fun newInstance() = FindOrRegisterFragment()
    }

    //    private var viewModel: FindOrRegisterViewModel? = null
    var appDisposable: AppDisposable = AppDisposable()
    private var isRegistered: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(context).inflate(R.layout.fragment_find_or_register, null, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        viewModel = ViewModelProviders.of(this, FragmentViewModelFactory(this@FindOrRegisterFragment))
//                .get(FindOrRegisterViewModel::class.java)

        setView()
    }

    fun setView() {
        if (!isAdded){
            return
        }
        if (isRegistered) {
            stepProgressTv?.visibility = View.GONE
            stepProgressLl?.visibility = View.GONE

            nameEt?.setTitleHint(getString(R.string.enter_your_house_key))
            nextBtn?.text = getString(R.string.find)
            headerTv?.text = getString(R.string.find_your_house)

            bottomText?.text = getString(R.string.don_t_have_a_house_key)
            bottomSubtext?.text = getString(R.string.register)

        } else {
            stepProgressTv?.visibility = View.VISIBLE
            stepProgressLl?.visibility = View.VISIBLE

            nameEt?.setTitleHint(getString(R.string.enter_a_unique_house_key))
            nextBtn?.text = getString(R.string.generate)
            headerTv?.text = getString(R.string.register_your_house)

            bottomText?.text = getString(R.string.already_have_a_house_key)
            bottomSubtext?.text = getString(R.string.find_your_house)
        }

        findOrRegisterBtn?.setOnClickListener {
            isRegistered = !isRegistered
            setView()
        }

        nextBtnProgress?.visibility = View.GONE
        nextBtn?.setOnClickListener {
            nextBtnProgress?.visibility = View.VISIBLE
            nextBtn?.text = ""
//            openAddUserFragment()
//            viewModel?.getAllHouseKeys()
//            searchHouseId()
            searchHouseKey(nameEt.mInputEt?.text.toString())
        }
    }

    fun searchHouseId() {
        val query = GetAllHouseIdsQuery.builder().build()
        ApolloService.buildApollo().query(query)
            .responseFetcher(ApolloResponseFetchers.NETWORK_ONLY)
            .enqueue(object : ApolloCall.Callback<GetAllHouseIdsQuery.Data>() {
                override fun onFailure(error: ApolloException) {
                    Log.d(TAG, error.toString())
                }

                override fun onResponse(@NotNull response: Response<GetAllHouseIdsQuery.Data>) {
//                    val userList = mutableListOf(response.data()!!).flatMap { data ->
//                        data.plankhana_houses_house().map { data ->
//                            data.id()
//                        }
//                    }
                    response.data()

//                    listItems = userList.toMutableList()
//
//                    iModuleListener.onHouseKeyResultApiSuccess(response.data())
                }
            })
    }

    fun addHouseKey(key: String) {
        // Init Query
        val keyMutation = AddHouseKeyMutation.builder().key(key).build()

//         Apollo runs query on background thread
        ApolloService.buildApollo().mutate(keyMutation)?.enqueue(object :
            ApolloCall.Callback<AddHouseKeyMutation.Data>() {
            override fun onFailure(error: ApolloException) {
//                iModuleListener.onHouseKeyAddedApiFailure(error.toString())
            }

            override fun onResponse(@NotNull response: Response<AddHouseKeyMutation.Data>) {
                response.data()
//                iModuleListener.onHouseKeyAddedApiSuccess(response.data())
//                Network.apolloClient.enableSubscriptions()
            }
        })

    }

    fun searchHouseKey(key: String) {
        val getNewPublicTodosQuery = SearchHouseKeyQuery.builder().key(key).build()
        ApolloService.buildApollo().query(getNewPublicTodosQuery)
            .responseFetcher(ApolloResponseFetchers.NETWORK_ONLY)
            .enqueue(object : ApolloCall.Callback<SearchHouseKeyQuery.Data>() {
                override fun onFailure(error: ApolloException) {
//                    iModuleListener.onHouseKeyResultApiFailure(error.toString())
                }

                override fun onResponse(@NotNull response: Response<SearchHouseKeyQuery.Data>) {
                    mutableListOf(response.data()!!).flatMap { data ->
                        data.plankhana_houses_house().map { data ->
                            data.id()
                            activity?.runOnUiThread {
                                openAddUserFragment()
                            }
                        }
                    }
                }
            })
    }

    private fun openAddUserFragment() {
        setView()
        if (isRegistered) {
            addFragment(SelectRoleFragment.newInstance(), AddBhaiyaFragment.TAG)
        } else {
            addFragment(AddBhaiyaFragment.newInstance(), AddBhaiyaFragment.TAG)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
//        viewModel?.onDestroy()
        appDisposable.dispose()
    }
}
