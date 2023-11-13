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

interface Listener {
    fun openImage(url: String)
}

class MessageAdapter(
    private val messages: List<Message>, private val mListener: Listener
) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder?>() {

    class MessageViewHolder(root: View) : RecyclerView.ViewHolder(root) {
        private val fromView = root.findViewById<TextView>(R.id.from)
        private val toView = root.findViewById<TextView>(R.id.to)
        private val timeView = root.findViewById<TextView>(R.id.time)
        val imageView = root.findViewById<ImageView>(R.id.imageView)
        private val msgView = root.findViewById<TextView>(R.id.msg)

        fun bind(message: Message) {
            fromView.text = message.from
            toView.text = message.to
            val dt = Date(message.time)
            timeView.text = DateFormat.format("yyyy-MM-dd kk:mm:ss", dt)
            msgView.text = if (message.data.Text != null) message.data.Text.text else ""
            imageView.visibility = if(message.data.Image != null) View.VISIBLE else View.GONE

            if(message.data.Image != null){
                //imageView.setImageBitmap(thumb)
                Picasso.get().load("http://213.189.221.170:8008/thumb/${message.data.Image.link}").error(
                    com.google.android.material.R.drawable.mtrl_ic_error).into(imageView)
            }else {
                imageView.setImageResource(R.drawable.image_selector)
            }

        }
    }

    override fun getItemCount() = messages.size

    override fun onBindViewHolder(
        holder: MessageViewHolder,
        position: Int
    ){
        holder.bind(messages[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int)
            : MessageViewHolder {
        val holder = MessageViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.message, parent, false)
        )
        holder.imageView.setOnClickListener {
            if(messages[holder.adapterPosition].data.Image != null)
                mListener.openImage(messages[holder.adapterPosition].data.Image!!.link)
        }
        return holder
    }
}