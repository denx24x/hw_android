package com.android_hw.hw4

import android.app.Application
import android.util.Log
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


class MyApp : Application() {
    private lateinit var retrofit: Retrofit
    lateinit var service: NetworkService
    var messages : ArrayList<Message> = ArrayList()
    var lastUpdatedPos = 0
    var runningPull = false
    private lateinit var moshi: Moshi
    private lateinit var adapter : JsonAdapter<List<Message>>
    private val TAG = "MyApp"

    override fun onCreate() {
        super.onCreate()
        instance = this
        moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
        adapter = moshi.adapter<List<Message>>(
            Types.newParameterizedType(
                List::class.java,
                Message::class.java
            ))

        retrofit = Retrofit.Builder()
            .baseUrl("http://213.189.221.170:8008")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            //.client(OkHttpProvider.getOkHttpClient())
            .build()

        service = retrofit.create(NetworkService::class.java)
        getMessagesFromCache()

    }


    companion object {
        lateinit var instance : MyApp
        private set
    }

    private fun getMessagesFromCache(){
        try{
            val file = File(cacheDir.absolutePath + "/messages.json")

            if(file.exists()){
                val input = FileInputStream(file)
                val str = input.readBytes().decodeToString()
                Log.i(TAG, str)
                val data = adapter.fromJson(str)
                if(data == null){
                    Log.i(TAG, "messages failed to load from cache")
                    return
                }
                messages += data
                lastUpdatedPos = messages.size
                Log.i(TAG, "messages loaded from cache")
            }

        }catch (e: Throwable){
            Log.i(TAG, "messages failed to load from cache ${e.message}")
        }
    }

    fun loadMessagesToCache(){
        try {
            val out = FileOutputStream(cacheDir.absolutePath + "/messages.json")
            out.write(adapter.toJson(messages).toByteArray())
            out.close()
            Log.i(TAG, "Messages saved to cache")
        } catch (e: Throwable) {
            Log.i(TAG, "Failed to save messages in cache")
        }
    }

}

