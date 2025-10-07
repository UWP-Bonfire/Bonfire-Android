package com.example.bonfire.messagesRecycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bonfire.R

// RecyclerView adapter for the scrollable messages view
class MessageAdapter(private val data: List<Message>) : RecyclerView.Adapter<MessageAdapter.ItemViewHolder>() {
    // Akin to onCreate method to initialize each instance (each message)
    inner class ItemViewHolder(view: View): RecyclerView.ViewHolder(view){
        val name: TextView = view.findViewById(R.id.message_user)
        val content: TextView = view.findViewById(R.id.message_text)
        val picture: ImageView = view.findViewById(R.id.message_profile)
    }

    // Define each entry's layout/look
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflatedView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.message_layout, parent, false)
        return ItemViewHolder(inflatedView)
    }

    //Set values to the views based on the position of the recyclerView
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val message : Message = data[position]
        holder.name.text = message.userName
        holder.content.text = message.content
        holder.picture.setImageResource(message.userProfile)
    }

    //  Total number of elements in recyclerView
    override fun getItemCount(): Int {
        return data.size
    }

}