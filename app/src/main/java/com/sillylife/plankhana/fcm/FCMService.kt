package com.sillylife.plankhana.fcm

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object FCMService {

    val BASE_URL = "https://fcm.googleapis.com"

    fun build(): FirebaseAPI {
        val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient())
                .build()
        return retrofit.create(FirebaseAPI::class.java)
    }

    private fun okHttpClient(): OkHttpClient {
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(interceptorWithOutBearer())
        httpClient.addInterceptor(httpLoggingInterceptor())
        httpClient.readTimeout(60, TimeUnit.SECONDS)
        httpClient.connectTimeout(60, TimeUnit.SECONDS)
        httpClient.interceptors().add(httpLoggingInterceptor())
        return httpClient.build()
    }

    fun interceptorWithOutBearer(): Interceptor {
        return Interceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
            requestBuilder.header("Authorization", "key=AIzaSyCgJlIuJIJfF6cll4NlNIJM2EZUesPIgWs")
            requestBuilder.header("content-type", "application/json")
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