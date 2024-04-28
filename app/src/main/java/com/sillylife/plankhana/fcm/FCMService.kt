package com.sillylife.plankhana.fcm

import com.sillylife.plankhana.models.Message
import com.sillylife.plankhana.models.responses.EmptyResponse
import io.reactivex.Observable
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.concurrent.TimeUnit


object FCMService {

    interface FirebaseAPI {
        @POST("/fcm/send")
        fun sendNotification(@Body message: Message): Observable<Response<EmptyResponse>>
    }

    val BASE_URL = "https://fcm.googleapis.com"
    const val key = ""

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
            requestBuilder.header("Authorization", "key=$key")
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
