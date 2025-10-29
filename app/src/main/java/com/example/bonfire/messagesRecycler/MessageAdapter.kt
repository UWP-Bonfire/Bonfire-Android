package com.example.bonfire.messagesRecycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bonfire.R
import com.google.type.DateTime

// RecyclerView adapter for the scrollable messages view
class MessageAdapter(private val data: ArrayList<Map<String, Any>?>) : RecyclerView.Adapter<MessageAdapter.ItemViewHolder>() {
    // Akin to onCreate method to initialize each instance (each message)
    inner class ItemViewHolder(view: View): RecyclerView.ViewHolder(view){
        val displayName: TextView = view.findViewById(R.id.message_user)
        val photoURL: ImageView = view.findViewById(R.id.message_profile)
        val text: TextView = view.findViewById(R.id.message_text)
//        val timestamp: DateTime
//        val uid: String
    }

    // Define each entry's layout/look
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflatedView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.message_layout, parent, false)
        return ItemViewHolder(inflatedView)
    }

    //Set values to the views based on the position of the recyclerView
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val message : Map<String, Any>? = data[position]
        holder.displayName.text = message?.get("displayName").toString()
        holder.text.text = message?.get("text").toString()
        //holder.photoURL.setImageResource(message.photoURL)
    }

    //  Total number of elements in recyclerView
    override fun getItemCount(): Int {
        return data.size
    }

}