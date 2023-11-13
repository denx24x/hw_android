package com.android_hw.hw4

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import java.util.*

interface ChannelListener {
    fun changeChannel(channel: String)
}

class ChannelAdapter(
    private val channels: List<String>, private val listener: ChannelListener
) : RecyclerView.Adapter<ChannelAdapter.ChannelViewHolder?>() {

    class ChannelViewHolder(root: View) : RecyclerView.ViewHolder(root) {
        val textView = root.findViewById<TextView>(R.id.channel)

        fun bind(channel: String) {
            textView.text = channel
        }
    }

    override fun getItemCount() = channels.size

    override fun onBindViewHolder(
        holder: ChannelViewHolder,
        position: Int
    ){
        holder.bind(channels[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            : ChannelViewHolder {
        val holder = ChannelViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.channel, parent, false)
        )
        holder.itemView.setOnClickListener{
            listener.changeChannel(holder.textView.text.toString())
        }

        return holder
    }
}