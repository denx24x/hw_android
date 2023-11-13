package com.android_hw.hw4

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android_hw.hw4.fragments.HomeFragment
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Body

class PullCallback (private val application: MyApp, private val channel : String): Callback<List<Message>> {
    private val TAG = "PullCallback"
    override fun onResponse(
        call: Call<List<Message>>,
        response: Response<List<Message>?>
    ) {
        val body: List<Message>? = response.body()
        if (response.isSuccessful && body != null) {
            Log.i(TAG, "success")
            if(body.isEmpty()) {
                notifyRecycler()
                return
            }
            for(element in body){
                application.mMessages.add(element)
            }
            Log.i(TAG, body.toString())
            application.service.getMessages(application.currentChannel, body[body.size - 1].id).enqueue(PullCallback(application, channel))
        } else {
            notifyRecycler()
            Log.i(TAG, "failure ${response.code()} $body")
        }
    }

    override fun onFailure(call: Call<List<Message>>, t: Throwable) {
        notifyRecycler()
        if (!call.isCanceled) {
            Log.i(TAG, "failure ${t.message}")
        }
    }

    private fun notifyRecycler(){
        application.runningPull = false
        /*
        if(caller.lastPos == caller.mMessages.size) return
        caller.mAdapter.notifyItemRangeInserted(
            caller.lastPos,
            caller.mMessages.size - caller.lastPos
        )

        caller.myRecyclerView.scrollToPosition(caller.mMessages.size - 1)
        caller.lastPos = caller.mMessages.size*/
        application.refreshListener?.eventPull(application.mMessages, channel)
    }
}

class SendCallback(private val context: Context) : Callback<ResponseBody> {
    private val TAG = "SendCallback"
    override fun onResponse(
        call: Call<ResponseBody>,
        response: Response<ResponseBody?>
    ) {
        val body: ResponseBody? = response.body()
        if (response.isSuccessful && body != null) {
            Log.i(TAG, "success")
        } else {
            if(response.code() >= 500){
                Toast.makeText(context, "Failed to send - internal server error", Toast.LENGTH_LONG).show()
            }else if (response.code() == 413){
                Toast.makeText(context, "Too big image!", Toast.LENGTH_LONG).show()
            }
            else {
                Toast.makeText(context, "Error sending message!", Toast.LENGTH_LONG).show()
            }
            Log.i(TAG, "error ${response.code()} $body")

        }
    }

    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
        if (!call.isCanceled) {
            Log.i(TAG, "failure ${t.message}")
            Toast.makeText(context, "Error sending message!", Toast.LENGTH_LONG).show()
        }
    }
}


class ChannelsCallback (private val channels: ArrayList<String>,
                    private val adapter: ChannelAdapter) : Callback<List<String>> {
    private val TAG = "ChannelsCallback"
    override fun onResponse(
        call: Call<List<String>>,
        response: Response<List<String>?>
    ) {
        val body: List<String>? = response.body()
        if (response.isSuccessful && body != null) {
            Log.i(TAG, "success")
            val was = channels.size
            channels += body
            adapter.notifyItemRangeInserted(was, channels.size - was)
        } else {
            Log.i(TAG, "failure ${response.code()} $body")
        }
    }

    override fun onFailure(call: Call<List<String>>, t: Throwable) {
        if (!call.isCanceled) {
            Log.i(TAG, "failure ${t.message}")
        }
    }

}
