package com.sillylife.plankhana.services

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Operation
import com.apollographql.apollo.api.ResponseField
import com.apollographql.apollo.cache.normalized.CacheKey
import com.apollographql.apollo.cache.normalized.CacheKeyResolver
import com.apollographql.apollo.cache.normalized.NormalizedCacheFactory
import com.apollographql.apollo.cache.normalized.lru.EvictionPolicy
import com.apollographql.apollo.cache.normalized.lru.LruNormalizedCacheFactory
import com.apollographql.apollo.cache.normalized.sql.ApolloSqlHelper
import com.apollographql.apollo.cache.normalized.sql.SqlNormalizedCacheFactory
import com.apollographql.apollo.subscription.WebSocketSubscriptionTransport
import com.sillylife.plankhana.BuildConfig
import com.sillylife.plankhana.PlanKhana
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit


object ApolloService {

    val GRAPHQL_ENDPOINT: String = "http://hasura-plankhana.herokuapp.com/v1/graphql"
    val GRAPHQL_WEBSOCKET_ENDPOINT: String = "wss://hasura-plankhana.herokuapp.com/v1/graphql"
    private val SQL_CACHE_NAME = "mktodo"

    fun buildApollo(): ApolloClient {
        return ApolloClient.builder()
            .serverUrl(GRAPHQL_ENDPOINT)
            .okHttpClient(okHttpClient())
            .normalizedCache(normalizedCacheFactory(), cacheKeyResolver())
            .subscriptionTransportFactory(
                WebSocketSubscriptionTransport.Factory(
                    GRAPHQL_WEBSOCKET_ENDPOINT,
                    okHttpClient()
                )
            )
            .build()
    }

    private fun normalizedCacheFactory(): NormalizedCacheFactory<*> {
        val context = PlanKhana.getInstance().applicationContext
        val apolloSqlHelper = ApolloSqlHelper(context, SQL_CACHE_NAME)

        return LruNormalizedCacheFactory(EvictionPolicy.NO_EVICTION)
            .chain(SqlNormalizedCacheFactory(apolloSqlHelper))
    }

    private fun cacheKeyResolver(): CacheKeyResolver = object : CacheKeyResolver() {
        override fun fromFieldRecordSet(
            field: ResponseField,
            recordSet: Map<String, Any>): CacheKey {
            if (recordSet.containsKey("todos")) {
                val id = recordSet["todos"] as String
                return CacheKey.from(id)
            }
            return CacheKey.NO_KEY
        }

        override fun fromFieldArguments(
            field: ResponseField,
            variables: Operation.Variables): CacheKey {
            return CacheKey.NO_KEY
        }
    }

    private fun okHttpClient(): OkHttpClient {
        val httpClient = OkHttpClient.Builder()
        httpClient.interceptors().add(interceptor(0))
        httpClient.readTimeout(60, TimeUnit.SECONDS)
        httpClient.connectTimeout(60, TimeUnit.SECONDS)
        if (BuildConfig.DEBUG)
            httpClient.interceptors().add(httpLoggingInterceptor())
        return httpClient.build()
    }

    private fun interceptor(cacheDuration: Long): Interceptor {
        return Interceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
            requestBuilder.addHeader("content-type", "application/json")
//            if(!CommonUtil.textIsEmpty(FirebaseAuthUserManager.getFirebaseAuthToken())){
//                requestBuilder.addHeader("Authorization", "Bearer ${FirebaseAuthUserManager.getFirebaseAuthToken()}")
//            }
            if (cacheDuration > 0) {
                requestBuilder.addHeader("Cache-Control", "public, max-age=$cacheDuration")
            }
//            requestBuilder.addHeader("lang", SharedPreferenceManager.getAppLanguageEnum().slug)
            val request = requestBuilder.build()
            val response = chain.proceed(request)
            response
        }
    }

    private fun httpLoggingInterceptor(): HttpLoggingInterceptor {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return httpLoggingInterceptor
    }
}