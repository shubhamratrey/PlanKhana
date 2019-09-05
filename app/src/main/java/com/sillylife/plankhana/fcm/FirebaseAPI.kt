package com.sillylife.plankhana.fcm

import com.sillylife.plankhana.models.Message
import com.sillylife.plankhana.models.responses.EmptyResponse
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface FirebaseAPI {
    @POST("/fcm/send")
    fun sendMessage(@Body message: Message): Observable<Response<EmptyResponse>>
}