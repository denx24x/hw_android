package com.android_hw.hw4.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android_hw.hw4.*

interface ChannelSelectListener {
    fun onChannelSelected(selected : String)
}

class ChatFragment : Fragment() {
    private lateinit var myRecyclerView : RecyclerView
    private lateinit var mAdapter : ChannelAdapter
    private val mChannels : ArrayList<String> = ArrayList()

    private lateinit var mApp : MyApp
    private lateinit var listener : ChannelSelectListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.chat_fragment, container, false)
        mApp = requireActivity().application as MyApp
        listener = context as ChannelSelectListener
        myRecyclerView = view.findViewById(R.id.channelRecyclerView)
        val viewManager =
            LinearLayoutManager(requireActivity())

        mAdapter = ChannelAdapter(mChannels, object : ChannelListener {
            override fun changeChannel(channel: String) {
                listener.onChannelSelected(channel)
            }
        })

        viewManager.stackFromEnd = true
        myRecyclerView.apply {
            layoutManager = viewManager
            adapter = mAdapter
        }

        mApp.service.getChannels().enqueue(
            ChannelsCallback(mChannels, mAdapter)
        )

        return view
    }
}