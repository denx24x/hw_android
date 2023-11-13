package com.android_hw.hw4

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class Message(
    @field:Json(name = "id") val id: Int,
    @field:Json(name = "from") val from: String,
    @field:Json(name = "to") val to: String,
    @field:Json(name = "time") val time: Long,
    @field:Json(name = "data") val data: MessageData)

@JsonClass(generateAdapter = true)
data class MessageData(@field:Json(name = "Text") val Text: Text?,
                       @field:Json(name = "Image") val Image: Image?)

@JsonClass(generateAdapter = true)
data class Text(@field:Json(name = "text") val text: String)

@JsonClass(generateAdapter = true)
data class Image(@field:Json(name = "link") val link: String)

fun getFileName(uri: Uri, context: Context): String? {
    var result: String? = null
    if (uri.scheme.equals("content")) {
        val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
        cursor.use { cursor ->
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if(index >= 0) {
                    result = cursor.getString(index)
                }
            }
        }
    }
    if (result == null) {
        result = uri.path
        val cut = result?.lastIndexOf('/')
        if (cut != -1) {
            if (cut != null) {
                result = result?.substring(cut + 1)
            }
        }
    }
    return result
}