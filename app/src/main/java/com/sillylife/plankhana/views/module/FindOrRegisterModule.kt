package com.sillylife.plankhana.views.module

import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.fetcher.ApolloResponseFetchers
import com.sillylife.plankhana.GetAllHouseIdsQuery
import com.sillylife.plankhana.SearchHouseKeyQuery
import com.sillylife.plankhana.models.responses.FindOrRegisterResponse
import org.jetbrains.annotations.NotNull

class FindOrRegisterModule(val iModuleListener: IModuleListener) : BaseModule() {

    fun searchHouseKey(getNewPublicTodosQuery: SearchHouseKeyQuery) {
        apolloClient.query(getNewPublicTodosQuery)
            .responseFetcher(ApolloResponseFetchers.NETWORK_ONLY)
            .enqueue(object : ApolloCall.Callback<SearchHouseKeyQuery.Data>() {
                override fun onFailure(error: ApolloException) {
                    iModuleListener.onHouseKeyResultApiFailure(error.toString())
                }

                override fun onResponse(@NotNull response: Response<SearchHouseKeyQuery.Data>) {
                    val userList = mutableListOf(response.data()!!).flatMap { data ->
                        data.plankhana_houses_house().map { data ->
                            data.id()
                        }
                    }

//                    listItems = userList.toMutableList()
//
//                    iModuleListener.onHouseKeyResultApiSuccess(response.data())
                }
            })
    }

    fun searchHouseId() {
        val query = GetAllHouseIdsQuery.builder().build()
        apolloClient.query(query)
            .responseFetcher(ApolloResponseFetchers.NETWORK_ONLY)
            .enqueue(object : ApolloCall.Callback<GetAllHouseIdsQuery.Data>() {
                override fun onFailure(error: ApolloException) {
                    iModuleListener.onHouseKeyResultApiFailure(error.toString())
                }

                override fun onResponse(@NotNull response: Response<GetAllHouseIdsQuery.Data>) {
                    val userList = mutableListOf(response.data()!!).flatMap { data ->
                        data.plankhana_houses_house().map { data ->
                            data.id()
                        }
                    }

//                    listItems = userList.toMutableList()
//
//                    iModuleListener.onHouseKeyResultApiSuccess(response.data())
                }
            })
    }

    fun addHouseKey(key: String) {
        // Init Query
//        val addTodoMutation = AddTodoMutation.builder().todo(key).isPublic(true).build()

        // Apollo runs query on background thread
//        appDisposable.add(apolloClient.mutate(addTodoMutation)?.enqueue(object :
//                ApolloCall.Callback<AddTodoMutation.Data>() {
//            override fun onFailure(error: ApolloException) {
//                iModuleListener.onHouseKeyAddedApiFailure(error.toString())
//            }
//
//            override fun onResponse(@NotNull response: Response<AddTodoMutation.Data>) {
//                iModuleListener.onHouseKeyAddedApiSuccess(response.data())
////                Network.apolloClient.enableSubscriptions()
//            }
//        }))

    }

    interface IModuleListener {
        fun onHouseKeyResultApiSuccess(response: FindOrRegisterResponse)
        fun onHouseKeyResultApiFailure(error: String)
        fun onHouseKeyAddedApiSuccess(response: FindOrRegisterResponse)
        fun onHouseKeyAddedApiFailure(error: String)
    }

}