package com.android_hw.hw4

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface NetworkService {
    @GET("/1ch")
    fun getMessages(
        @Query("lastKnownId") lastKnownId : Int,
        @Query("limit") limit : Int = 50
    ) : Call<List<Message>>

    @POST("/1ch")
    fun sendMessage(@Body msg : Message) : Call<ResponseBody>

    @Multipart
    @POST("/1ch")
    fun sendImage(@Part("msg") msg : RequestBody , @Part pic : MultipartBody.Part) : Call<ResponseBody>
}