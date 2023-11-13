package com.android_hw.hw4

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import org.json.JSONObject

class MyBroadcastReceiver(private var msgAdapter: MessageAdapter,
                          private var msgDataset: ArrayList<Message>,
                          private var recyclerview: RecyclerView) : BroadcastReceiver() {

    private val TAG = "MyBroadcastReceiver"
    private val SUCCESS = 1
    val PARAM_EXCEPTION = "exception"
    private val RESULT_PULL = 1100
    private val RESULT_IMG_THUMB = 2200
    private val RESULT_IMG_BIG = 2201

    override fun onReceive(context: Context?, intent: Intent) {
        Log.d(TAG, "Received intent")
        val resultCode = intent.getIntExtra("success", 0)
        if (resultCode == SUCCESS) {
            onSuccess(context, intent.getBundleExtra("data")!!)
        } else {
            onError(context)
        }
    }


    private fun onSuccess(context: Context?, data: Bundle){
        Log.d(TAG, "Success")
        when (data.getInt("code", 0)) {
            RESULT_PULL -> {
                onPull(context, data)
            }
            RESULT_IMG_THUMB -> {
                onImageThumb(context, data)
            }
            RESULT_IMG_BIG -> {
                onImageBig(context, data)
            }
        }
    }

    private fun onImageThumb(context: Context?, data: Bundle){
        Log.d(TAG, "onImageThumb")
        val bmp: Bitmap = data.getParcelable("result")!!
        val pos = data.getInt("pos", 0)

        msgDataset[pos].thumb_data = bmp


        msgAdapter.notifyItemChanged(pos)
    }

    private fun onImageBig(context: Context?, data: Bundle){
        Log.d(TAG, "onImageBig")
        if(context == null) return
        val intent = Intent(context , FullImageActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }
    private fun onPull(context: Context?, data: Bundle) {
        Log.d(TAG, "onPull")
        val jdata = JSONArray(data.getString("result"))
        if(jdata.length() == 0) return
        //Log.i(TAG, jdata.toString())
        val lastPos = msgDataset.count()
        for (i in 0 until jdata.length()){
            val item : JSONObject = jdata.getJSONObject(i)
            //Log.i(TAG, item.toString())
            msgDataset.add(
                parseMessageFromJSON(item)
            )
        }
        msgAdapter.notifyItemRangeInserted(lastPos, jdata.length())
        recyclerview.scrollToPosition(msgDataset.size - 1)
    }

    private fun onError(context: Context?) {
        Log.i(TAG, "Error!")
    }

}

