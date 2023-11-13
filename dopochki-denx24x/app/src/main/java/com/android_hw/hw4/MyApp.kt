package com.android_hw.hw4

import android.R
import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.android_hw.hw4.fragments.HomeFragment
import com.android_hw.hw4.fragments.HomeListener
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory



val USERNAME = "Ryan Gosling"
class MyApp : Application() {
    private lateinit var retrofit: Retrofit
    lateinit var service: NetworkService

    var runningPull = false
    private lateinit var moshi: Moshi
    private lateinit var adapter : JsonAdapter<List<Message>>
    private val TAG = "MyApp"
    var cameraEnabled = false
    public var currentChannel = "2@channel"
    var refreshListener : HomeListener? = null
    val mMessages : ArrayList<Message> = ArrayList()
    var cameraCaller : Any? = null


    public fun switchChannel(newChannel : String){
        Log.i(TAG, "channel switch from $currentChannel to $newChannel")
        //loadMessagesToCache()
        currentChannel = newChannel
        mMessages.clear()
        updateMessages(currentChannel)
        //refreshListener?.event()
        //getMessagesFromCache()
    }
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
        updateMessages(currentChannel)
        //getMessagesFromCache()

    }

    fun registerChat(listener: HomeListener?){
        refreshListener = listener
        //listener?.event(mMessages)
    }

    fun updateMessages(channel : String){
        if(runningPull) return
        runningPull = true
        service.getMessages(currentChannel, if (mMessages.isEmpty()) 0 else mMessages.last().id).enqueue(
            PullCallback(this, channel)
        )
    }

    companion object {
        lateinit var instance : MyApp
        private set
    }
    /*
    private fun getMessagesFromCache(){
        try{
            val file = File(cacheDir.absolutePath + "/${currentChannel}_messages.json")

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
            val out = FileOutputStream(cacheDir.absolutePath + "/${currentChannel}_messages.json")
            out.write(adapter.toJson(messages).toByteArray())
            out.close()
            Log.i(TAG, "Messages saved to cache")
        } catch (e: Throwable) {
            Log.i(TAG, "Failed to save messages in cache ${e.message}")
        }
    }*/

}

