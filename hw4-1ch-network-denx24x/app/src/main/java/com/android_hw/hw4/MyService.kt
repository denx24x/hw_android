package com.android_hw.hw4

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.*
import android.provider.OpenableColumns
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.LinkedList
import java.util.Queue



fun getFileName(uri: Uri, context: Context): String {
    var result: String? = null
    if (uri.scheme == "content") {
        val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
        try {
            if(cursor != null) {
                val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (cursor.moveToFirst() && index >= 0) {
                    result = cursor.getString(index)
                }
            }
        } finally {
            cursor?.close()
        }
    }
    if (result == null) {
        result = uri.path
        val cut = result!!.lastIndexOf('/')
        if (cut != -1) {
            result = result.substring(cut + 1)
        }
    }
    return result
}
class MyService : Service() {
    private val TAG = "IntentServiceLogs"
    private val SERVICE_URL =  "http://213.189.221.170:8008"
    private val RESULT_PULL = 1100
    private val RESULT_IMG_THUMB = 2200
    private val RESULT_IMG_BIG = 2201
    private val SUCCESS = 1
    private var lastKnownId = 0
    private val mHandler = Handler(Looper.getMainLooper())
    private val buffer : Queue<Intent> = LinkedList()

    private val data: JSONArray = JSONArray()
    private val USERNAME = "Ryan Gosling"

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "onCreate")
    }

    private fun  handleSendImage(intent: Intent){
        Log.i(TAG, "handleSendImage")
        val filename : String = intent.getStringExtra("file")!!
        val file : Uri = Uri.parse(filename)
        val link = "$SERVICE_URL/1ch"
        val msg = JSONObject()
        msg.put("from", USERNAME)
        //msg.put("to", "1@channel")
        val url = URL(link)
        val connection = url.openConnection() as HttpURLConnection
        connection.doOutput = true
        connection.doInput = true
        connection.requestMethod = "POST"
        val boundary = MultipartTool.generateBoundary()
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")
        val outputStream = connection.outputStream
        val tool = MultipartTool(outputStream, boundary)
        try{
            tool.appendJsonField("msg", msg.toString())
            Log.i(TAG, filename)
            val inputStream = contentResolver.openInputStream(file)!!
            val data : ByteArray = inputStream.readBytes()
            Log.i(TAG, this.contentResolver.getType(file)!!)

            tool.appendFile("pic",  data, this.contentResolver.getType(file), getFileName(file, this))
            inputStream.close()
        } finally {
            tool.close()
            outputStream.close()
        }
        val code = connection.responseCode
        val out = if (code in 200..299) System.out else System.err
        out.println(connection.responseMessage)
        connection.disconnect()
    }
    private fun handlePull(intent: Intent){
        Log.i(TAG, "handlePull")
        data.length()
        val channel = intent.getStringExtra("channel")
        val ans = JSONArray()
        if(intent.getBooleanExtra("fromBegin", false)){
            for(i in 0 until data.length()){
                ans.put(data[i])
            }
        }

        while (true) {
            val url = "$SERVICE_URL/$channel?limit=50&lastKnownId=$lastKnownId"
            Log.i(TAG, url)
            val u = URL(url)
            val c = u.openConnection() as HttpURLConnection
            c.connect()
            val status = c.responseCode
            var jObject: JSONArray?
            when (status) {
                200, 201 -> {
                    val br = BufferedReader(InputStreamReader(c.inputStream))
                    val sb = StringBuilder()
                    var output: String?
                    while (br.readLine().also { output = it } != null) {
                        sb.append(output)
                    }
                    Log.d(TAG, sb.toString())
                    jObject = JSONArray(sb.toString())
                    for (g in 0 until jObject.length()){
                        val item : JSONObject = jObject.getJSONObject(g)
                        lastKnownId = item.getInt("id")
                        data.put(item)
                        ans.put(item)
                    }
                    if (jObject.length() == 0) break
                }
            }
        }
        val bundle = Bundle()
        bundle.putInt("code", RESULT_PULL)
        bundle.putString("result", ans.toString())
        //bundle.putString("result", ans.toString())
        sendBroadcast(SUCCESS, bundle)
    }

    private fun handleSendText(intent: Intent){
        val channel = intent.getStringExtra("channel")!!
        val text = intent.getStringExtra("text")!!
        intent.getStringExtra("image")
        val msg = JSONObject()
        msg.put("from", USERNAME)
        msg.put("to", channel)
        val data = JSONObject()
        val textJson = JSONObject()
        textJson.put("text", text)
        data.put("Text", textJson)
        msg.put("data", data)
        Log.d(TAG, msg.toString())
        val con = URL("$SERVICE_URL/1ch").openConnection() as HttpURLConnection
        con.requestMethod = "POST"
        con.setRequestProperty("Content-Type", "application/json")
        val os = con.outputStream
        val writer = BufferedWriter(OutputStreamWriter(os, "UTF-8"))
        writer.write(msg.toString())
        writer.flush()
        writer.close()
        os.close()
        con.connect()
        Log.d(TAG, con.responseMessage)
        val status = con.responseCode

        when (status) {
            200, 201 -> {
                val br = BufferedReader(InputStreamReader(con.inputStream))
                val sb = StringBuilder()
                var output: String?
                while (br.readLine().also { output = it } != null) {
                    sb.append(output)
                }
                Log.d(TAG, sb.toString())
            }
        }
    }

    private fun handleImageThumb(intent: Intent){
        val path = intent.getStringExtra("path")
        val pos = intent.getIntExtra("pos", 0)
        val id = intent.getIntExtra("id", 0)
        val filepath = cacheDir.absolutePath + '/' + id.toString() + "_thumb"
        val file = File(filepath)
        val bmp: Bitmap?

        if(file.exists()){
            try {
                bmp = BitmapFactory.decodeStream(FileInputStream(file))
                Log.i(TAG, "Thumb received from $filepath")
            } catch (e: IOException) {
                Log.i(TAG, "Thumb failed to load from cache $filepath")
                return
            }
        }else {
            val url = URL("$SERVICE_URL/thumb/$path")

            try {
                bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                Log.i(TAG, "Thumb received from $url")
                Log.i(TAG, bmp.toString())
            } catch (_: Throwable) {
                Log.i(TAG, "Thumb failed to load $path")
                return
            }

            try {
                val out = FileOutputStream(file)
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, out)
                out.close()
                Log.i(TAG, "Thumb has been written to $filepath")
            } catch (e: IOException) {
                Log.i(TAG, "Thumb failed to save in cache $filepath")
                return
            }
        }

        if(bmp == null){
            Log.w(TAG, "Thumb failed to load - $id!")
            return

        }

        val bundle = Bundle()
        bundle.putInt("code", RESULT_IMG_THUMB)
        bundle.putParcelable("result", bmp)
        bundle.putInt("pos", pos)

        sendBroadcast(SUCCESS, bundle)
    }

    private fun handleImageBig(intent: Intent){
        val path = intent.getStringExtra("path")
        val filepath = cacheDir.absolutePath + "/big_image"
        val file = File(filepath)
        val bmp: Bitmap?

        if(file.exists()){
            try {
                file.delete()
                Log.i(TAG, "File deleted $filepath")
            } catch (e: IOException) {
                Log.i(TAG, "Failed deleting file $filepath")
            }
        }

        val url = URL("$SERVICE_URL/img/$path")

        try {
            bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
            Log.i(TAG, "Big image received from $url")
            Log.i(TAG, bmp.toString())
        } catch (_: Throwable) {
            Log.i(TAG, "Big image failed to load $path")
            return
        }

        try {
            val out = FileOutputStream(file)
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.close()
            Log.i(TAG, "Big image has been written to $filepath")
        } catch (e: IOException) {
            Log.i(TAG, "Big image failed to save in cache $filepath")
            return
        }finally {
            bmp.recycle()
        }

        if(bmp == null){
            Log.w(TAG, "Big image failed to load - $url!")
            return
        }

        val bundle = Bundle()
        bundle.putInt("code", RESULT_IMG_BIG)
        sendBroadcast(SUCCESS, bundle)
    }

    private fun sendBroadcast(success: Int, data: Bundle?) {
        Log.i(TAG, "sending response with code $success")
        val intent =
            Intent("MyService")
        intent.putExtra("success", success)
        intent.putExtra("data", data)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private fun moveQueue(){
        if(buffer.size == 0) return
        val intent = buffer.remove()
        val type = intent.getStringExtra("request")
        Thread {
            try {
                if (type == "PULL_MESSAGES") handlePull(intent)
                if (type == "SEND_TEXT") handleSendText(intent)
                if (type == "SEND_IMAGE") handleSendImage(intent)
                if (type == "GET_IMAGE_THUMB") handleImageThumb(intent)
                if (type == "GET_IMAGE_BIG") handleImageBig(intent)
            }finally {
                mHandler.post{
                    moveQueue()
                }
                // notify to start new task
            }
        }.start()
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if(intent == null){return START_NOT_STICKY}

        Log.i(TAG, "onHandleIntent")
        buffer.add(intent)
        if(buffer.size == 1) moveQueue()
        return START_NOT_STICKY
    }


    override fun onBind(intent: Intent?): IBinder {
        return MyBinder()
    }

    inner class MyBinder: Binder(){
        fun getMyService() = this@MyService
    }

}

class MyServiceConnection : ServiceConnection {
    private var mService: MyService? = null
    var mBound = false

    override fun onServiceConnected(className: ComponentName, service: IBinder) {
        val binder = service as MyService.MyBinder
        mService = binder.getMyService()
        mBound = true
    }

    override fun onServiceDisconnected(arg0: ComponentName) {
        mBound = false
    }
}