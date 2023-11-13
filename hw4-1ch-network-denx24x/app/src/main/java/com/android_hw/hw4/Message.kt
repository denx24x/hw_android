package com.android_hw.hw4

import android.graphics.Bitmap
import org.json.JSONObject


data class Message(val id: Int, val from: String, val to: String, val msg: String, val time: String, val thumb : String? = null, var thumb_data : Bitmap? = null)

fun parseMessageFromJSON(item : JSONObject) : Message {

    return Message(
        id = item.getInt("id"),
        from = item.getString("from"),
        to = item.getString("to"),
        msg = if (item.getJSONObject("data").has("Text")) item.getJSONObject("data").getJSONObject("Text").getString("text") else "",
        time = item.getString("time"),
        thumb = if (item.getJSONObject("data").has("Image")) item.getJSONObject("data").getJSONObject("Image").getString("link") else null
    )
}
