package com.android_hw.hw3

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class ContactAdapter(
    private val users: List<Contact>,
    private val onClick: (Contact) -> Unit
) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder?>() {
    class ContactViewHolder(root: View) : RecyclerView.ViewHolder(root) {
        val firstNameView = root.findViewById<TextView>(R.id.first_name)
        val lastNameView = root.findViewById<TextView>(R.id.last_name)
        val imageView = root.findViewById<ImageView>(R.id.imageView)

        fun bind(user: Contact) {
            firstNameView.text = user.name
            lastNameView.text = user.phoneNumber
            if(user.image != "N/A"){
                try {
                    imageView.setImageURI(Uri.parse(user.image))
                } catch (e: Throwable){
                    Log.i("!", user.image)}
            }

        }
    }

    override fun getItemCount() = users.size

    override fun onBindViewHolder(
        holder: ContactViewHolder,
        position: Int
    ) = holder.bind(users[position])

    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int)
            : ContactViewHolder {
        val holder = ContactViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.list_item, parent, false)
        )
        holder.firstNameView.setOnClickListener {
            onClick(users[holder.absoluteAdapterPosition])
        }
        return holder
    }


}