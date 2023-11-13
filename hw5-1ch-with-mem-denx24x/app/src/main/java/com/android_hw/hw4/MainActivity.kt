package com.android_hw.hw4

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private lateinit var myRecyclerView : RecyclerView
    private lateinit var mImageButton : ImageButton
    private lateinit var mButtonSend : ImageButton
    private lateinit var mEditText: EditText
    private lateinit var mRefresh: ImageButton
    private lateinit var mMessageReceiver: BroadcastReceiver
    private lateinit var connection : MyServiceConnection


    private val SELECT_PICTURE = 1
    private var selectedImage : String? = null

    private fun pickImage() {
        val i = Intent()
        i.type = "image/*"
        i.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE)
    }

    private fun sendMessage(){
        val intent = Intent(this, MyService::class.java)
        if(selectedImage != null){
            intent.putExtra("request", "SEND_IMAGE")
            intent.putExtra("channel", "1@channel")
            intent.putExtra("file", selectedImage)
            //intent.putExtra("image", selectedImage.toString())
            startService(intent)
            mImageButton.isSelected = false
            selectedImage = null
        }else{
            if(mEditText.text.toString() == "") return
            intent.putExtra("request", "SEND_TEXT")
            intent.putExtra("channel", "1@channel")
            intent.putExtra("text", mEditText.text.toString())
            //intent.putExtra("image", selectedImage.toString())
            startService(intent)
            mEditText.text.clear()
        }

        //UpdateMessages()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SELECT_PICTURE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                mImageButton.isSelected = false
                selectedImage = null
                return
            }
            mImageButton.isSelected = true
            Log.i(TAG, data.data.toString())
            selectedImage = data.data.toString()
        }else{
            mImageButton.isSelected = false
            selectedImage = null
        }
    }

    private fun updateMessages(){
        val intent = Intent(this, MyService::class.java)
        intent.putExtra("request", "PULL_MESSAGES")
        intent.putExtra("channel", "1ch")
        startService(intent)
    }

    private fun getAllMessages(){
        val intent = Intent(this, MyService::class.java)
        intent.putExtra("request", "PULL_MESSAGES")
        intent.putExtra("channel", "1ch")
        intent.putExtra("fromBegin", true)
        startService(intent)
    }

    override fun onDestroy() {
        Log.i(TAG, "DESTROY")
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver)
        super.onDestroy()
    }

    override fun onPause() {
        super.onPause()
        Log.i(TAG, "PAUSE")
        //LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver)
    }

    override fun onStop() {
        super.onStop()
        Log.i(TAG, "STOP")
        unbindService(connection)
        connection.mBound = false
    }

    override fun onStart() {
        super.onStart()
        Intent(this, MyService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        connection = MyServiceConnection()

        mImageButton = findViewById(R.id.pick_image)
        mImageButton.setOnClickListener {
            pickImage()
        }

        mButtonSend = findViewById(R.id.send)
        mButtonSend.setOnClickListener{
            sendMessage()
        }

        mRefresh = findViewById(R.id.refresh)
        mRefresh.setOnClickListener {
            updateMessages()
        }

        mEditText = findViewById(R.id.editText)

        myRecyclerView = findViewById(R.id.myRecyclerView)
        val viewManager =
            LinearLayoutManager(this)
        val dataset: ArrayList<Message> = ArrayList()
        val msgAdapter = MessageAdapter(dataset, object : Listener {
            override fun onView(pos: Int) {
                if(dataset[pos].thumb != null && dataset[pos].thumb_data == null){
                    val intent = Intent(applicationContext, MyService::class.java)
                    intent.putExtra("request", "GET_IMAGE_THUMB")
                    intent.putExtra("path", dataset[pos].thumb)
                    intent.putExtra("pos", pos)
                    intent.putExtra("id", dataset[pos].id)
                    startService(intent)
                }
            }

            override fun openImage(url: String) {
                val intent = Intent(applicationContext, MyService::class.java)
                intent.putExtra("request", "GET_IMAGE_BIG")
                intent.putExtra("path", url)
                startService(intent)
            }
        })
        mMessageReceiver = MyBroadcastReceiver(msgAdapter, dataset, myRecyclerView)
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
            IntentFilter("MyService")
        )
        viewManager.stackFromEnd = true
        myRecyclerView.apply {
            layoutManager = viewManager
            adapter = msgAdapter
        }
        getAllMessages()
    }


}

