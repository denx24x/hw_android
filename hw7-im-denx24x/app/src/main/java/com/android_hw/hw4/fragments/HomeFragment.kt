package com.android_hw.hw4.fragments

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android_hw.hw4.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

interface HomeListener {
    fun eventPull(data : ArrayList<Message>, channelReceived: String)
}

class HomeFragment : Fragment(), HomeListener {
    lateinit var myRecyclerView : RecyclerView
    private lateinit var mImageButton : ImageButton
    private lateinit var mButtonSend : ImageButton
    private lateinit var mEditText: EditText
    private lateinit var mRefresh: ImageButton
    private var channel : String = ""

    var mMessages : ArrayList<Message> = ArrayList()
    var lastPos = 0
    lateinit var mAdapter: MessageAdapter

    private lateinit var mApp : MyApp

    private val SELECT_PICTURE = 1
    private var selectedImage : String? = null

    private val TAG = "HomeFragment"

    override fun onStop() {
        super.onStop()
        //mApp.loadMessagesToCache()
        Log.i(TAG, "STOP")
    }

    private fun pickImage() {
        val i = Intent()
        i.type = "image/*"
        i.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(i,  "Select Picture"), SELECT_PICTURE)
    }

    override fun eventPull(data : ArrayList<Message>, channelReceived : String){
        Log.i(TAG, " clear " + data.toString())
        Log.i(TAG, getResources().getResourceName(this.id))
        if(channelReceived != mApp.currentChannel){
            return
        }

        if(channelReceived == channel) {
            val len = data.size - mMessages.size
            mMessages += data.subList(mMessages.size, data.size)
            if (::mAdapter.isInitialized) {
                mAdapter.notifyItemRangeInserted(mMessages.size - len, len)
                myRecyclerView.scrollToPosition(mMessages.size - 1)
            }
        }else{
            channel = mApp.currentChannel
            mMessages.clear()
            mMessages += data
            if (::mAdapter.isInitialized) {
                mAdapter.notifyDataSetChanged()
                myRecyclerView.scrollToPosition(mMessages.size - 1)
            }
        }
    }

    private fun sendMessage(){
        if(selectedImage != null){
            val file : Uri = Uri.parse(selectedImage)
            if (view == null) return
            val inputStream = requireView().context.contentResolver.openInputStream(file)!!
            val filePart = MultipartBody.Part.createFormData(
                "pic",
                "${System.currentTimeMillis()}-${getFileName(file, requireView().context)}",
                RequestBody.create(
                    requireView().context.contentResolver.getType(file)?.toMediaType(),
                    inputStream.readBytes()
                )
            )
            inputStream.close()

            val messagePart =
                "{\"from\": \"${USERNAME}\", \"to\": \"${mApp.currentChannel}\"}"
                    .toRequestBody("application/json".toMediaType())


            mApp.service.sendImage(
                messagePart, filePart
            ).enqueue(SendCallback(requireView().context))
            mImageButton.isSelected = false
            selectedImage = null
        }else{
            if(mEditText.text.toString() == "") return
            mApp.service.sendMessage(
                Message(
                id = 0,
                from = USERNAME,
                to = "${mApp.currentChannel}",
                time = System.currentTimeMillis(),
                data = MessageData(
                    Image = null,
                    Text = Text(mEditText.text.toString())
                )
            )
            ).enqueue(SendCallback(requireView().context))

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.i("onCreateView", "CHAT")
        val view = inflater.inflate(R.layout.home_fragment, container, false)
        mApp = (requireActivity().application as MyApp)

        mMessages.clear()
        mMessages += mApp.mMessages

        mImageButton = view.findViewById(R.id.pick_image)
        mImageButton.setOnClickListener {
            pickImage()
        }

        mButtonSend = view.findViewById(R.id.send)
        mButtonSend.setOnClickListener{
            sendMessage()
        }

        mRefresh = view.findViewById(R.id.refresh)
        mRefresh.setOnClickListener {
            mApp.updateMessages(mApp.currentChannel)
        }

        mEditText = view.findViewById(R.id.editText)

        myRecyclerView = view.findViewById(R.id.myRecyclerView)
        val viewManager =
            LinearLayoutManager(requireActivity())

        mAdapter = MessageAdapter(mMessages, object : Listener {
            override fun openImage(url: String) {
                val intent = Intent(view.context , FullImageActivity::class.java)
                intent.putExtra("link", url)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                view.context.startActivity(intent)
            }
        })

        viewManager.stackFromEnd = true
        myRecyclerView.apply {
            layoutManager = viewManager
            adapter = mAdapter
        }
        //updateMessages()
        return view
    }
}