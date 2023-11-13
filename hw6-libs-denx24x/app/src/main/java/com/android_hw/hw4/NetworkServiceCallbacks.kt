package com.android_hw.hw4

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PullCallback (private val adapter: MessageAdapter,
                    private val recyclerView: RecyclerView,
                    private val application: MyApp) : Callback<List<Message>> {
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
                application.messages.add(element)
            }

            //onApiResponse(body as ArrayList<DummyObject?>)
            application.service.getMessages( body[body.size - 1].id).enqueue(PullCallback(adapter, recyclerView, application))
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
        if(application.lastUpdatedPos == application.messages.size) return
        adapter.notifyItemRangeInserted(
            application.lastUpdatedPos,
            application.messages.size - application.lastUpdatedPos
        )

        recyclerView.scrollToPosition(application.messages.size - 1)
        application.lastUpdatedPos = application.messages.size
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
