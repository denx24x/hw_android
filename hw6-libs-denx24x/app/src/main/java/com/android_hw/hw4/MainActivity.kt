package com.android_hw.hw4

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody.Part.Companion.createFormData
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody


class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private val USERNAME = "Ryan Gosling"
    private lateinit var myRecyclerView : RecyclerView
    private lateinit var mImageButton : ImageButton
    private lateinit var mButtonSend : ImageButton
    private lateinit var mEditText: EditText
    private lateinit var mRefresh: ImageButton

    private lateinit var mMessages : ArrayList<Message>
    private lateinit var mAdapter: MessageAdapter

    private lateinit var mApp : MyApp

    private val SELECT_PICTURE = 1
    private var selectedImage : String? = null

    override fun onStop() {
        super.onStop()
        mApp.loadMessagesToCache()
        Log.i(TAG, "STOP")
    }
    private fun pickImage() {
        val i = Intent()
        i.type = "image/*"
        i.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE)
    }

    private fun sendMessage(){
        if(selectedImage != null){
            val file : Uri = Uri.parse(selectedImage)

            val inputStream = contentResolver.openInputStream(file)!!
            val filePart = createFormData(
                "pic", "${System.currentTimeMillis()}-${getFileName(file, this)}", RequestBody.create(
                    this.contentResolver.getType(file)?.toMediaType(),
                    inputStream.readBytes()
                )
            )
            inputStream.close()

            val messagePart =
                "{\"from\": \"${USERNAME}\"}"
                    .toRequestBody("application/json".toMediaType())


            mApp.service.sendImage(
                messagePart, filePart
            ).enqueue(SendCallback(this))
            mImageButton.isSelected = false
            selectedImage = null
        }else{
            if(mEditText.text.toString() == "") return
            mApp.service.sendMessage(Message(
                id = 0,
                from = USERNAME,
                to = "1@channel",
                time = System.currentTimeMillis(),
                data = MessageData(
                    Image = null,
                    Text = Text(mEditText.text.toString())
                )
            )).enqueue(SendCallback(this))

            mEditText.text.clear()
        }

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
        if(mApp.runningPull) return
        mApp.runningPull = true
        mApp.service.getMessages(if (mMessages.isEmpty()) 0 else mMessages.last().id).enqueue(PullCallback(mAdapter, myRecyclerView, mApp))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mApp = application as MyApp
        mMessages = mApp.messages

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

        mAdapter = MessageAdapter(mMessages, object : Listener {
            override fun openImage(url: String) {
                val intent = Intent(applicationContext , FullImageActivity::class.java)
                intent.putExtra("link", url)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                applicationContext.startActivity(intent)
            }
        })

        viewManager.stackFromEnd = true
        myRecyclerView.apply {
            layoutManager = viewManager
            adapter = mAdapter
        }

        updateMessages()
    }
}

