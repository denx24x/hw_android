package com.android_hw.hw4

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import okhttp3.ResponseBody
import retrofit2.http.*

interface NetworkService {
    @GET("/channel/{channel}")
    fun getMessages(
        @Path("channel") channel : String,
        @Query("lastKnownId") lastKnownId : Int,
        @Query("limit") limit : Int = 50
    ) : Call<List<Message>>

    @POST("/1ch")
    fun sendMessage(@Body msg : Message) : Call<ResponseBody>

    @Multipart
    @POST("/1ch")
    fun sendImage(@Part("msg") msg : RequestBody , @Part pic : MultipartBody.Part) : Call<ResponseBody>

    @GET("/channels")
    fun getChannels() : Call<List<String>>
}