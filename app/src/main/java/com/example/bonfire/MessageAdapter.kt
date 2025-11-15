package com.example.bonfire

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewManager
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Date

// RecyclerView adapter for the scrollable messages view
class MessageAdapter(private val data: ArrayList<Map<String, Any>?>, val inPrivateChat: Boolean, val uid:String) : RecyclerView.Adapter<MessageAdapter.ItemViewHolder>() {
    val helper = Helper()

    // Akin to onCreate method to initialize each instance (each message)
    inner class ItemViewHolder(view: View): RecyclerView.ViewHolder(view){
        val displayNameTextView: TextView = view.findViewById(R.id.message_user)
        val photoURLTextView: ImageView = view.findViewById(R.id.message_profile)
        val textTextView: TextView = view.findViewById(R.id.message_text)
        val timestampTextView: TextView = view.findViewById(R.id.message_timestamp)
        val checkReadImageView: ImageView = view.findViewById(R.id.check_read)
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
        holder.displayNameTextView.text = message?.get("displayName").toString()
        holder.textTextView.text = message?.get("text")?.toString()
        holder.timestampTextView.text = formatTimestampToString(message?.get("timestamp") as Timestamp)
        holder.photoURLTextView.setImageResource(helper.getAvatarId(message["photoURL"] as String?))

        // Only display read marks in DMs
        // if most recent show check mark (sent) or double check mark (read)
        if(inPrivateChat && position == itemCount - 1 && (message["senderId"] == uid)){
            val messageRead = message["read"] as Boolean
            val checkReadImageViewId = if (messageRead) R.drawable.double_check else R.drawable.check
            holder.checkReadImageView.setImageResource(checkReadImageViewId)
            holder.checkReadImageView.visibility = View.VISIBLE
        }
    }

    fun formatTimestampToString(timestamp: Timestamp): String{
        val timestampDate:Date = timestamp.toDate()
        val dateFormat = SimpleDateFormat("hh:mm a")
        return dateFormat.format(timestampDate)
    }

    //  Total number of elements in recyclerView
    override fun getItemCount(): Int {
        return data.size
    }
}