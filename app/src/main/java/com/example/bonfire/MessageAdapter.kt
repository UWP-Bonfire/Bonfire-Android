package com.example.bonfire

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.Date
import com.example.bonfire.ChatActivity.Companion.userAvatars

// RecyclerView adapter for the scrollable messages view
class MessageAdapter(private val data: ArrayList<Map<String, Any>?>) : RecyclerView.Adapter<MessageAdapter.ItemViewHolder>() {
    val db = Firebase.firestore

    // Akin to onCreate method to initialize each instance (each message)
    inner class ItemViewHolder(view: View): RecyclerView.ViewHolder(view){
        val displayNameTextView: TextView = view.findViewById(R.id.message_user)
        val photoURLTextView: ImageView = view.findViewById(R.id.message_profile)
        val textTextView: TextView = view.findViewById(R.id.message_text)
        val timestampTextView: TextView = view.findViewById(R.id.message_timestamp)
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

        // Load icon from uid
        val uid : String = message["uid"].toString()
        holder.photoURLTextView.setImageResource(userAvatars[uid]?: R.drawable.default_pfp)
    }

    fun formatTimestampToString(timestamp: Timestamp): String{
        val timestampDate:Date = timestamp.toDate()
        val dateFormat = SimpleDateFormat("MM/dd/yyyy HH:mm")
        return dateFormat.format(timestampDate)
    }

    //  Total number of elements in recyclerView
    override fun getItemCount(): Int {
        return data.size
    }

    // ex. set message with "avatar" = "/images/icon1" to R.id.icon1
    // --- Avatar mapping --- (terribly hardcoded)
    fun getAvatarId(avatarPath: String?) : Int {
        return when (avatarPath) {
            "/images/icon1.png" -> R.drawable.icon1
            "/images/icon2.png" -> R.drawable.icon2
            "/images/icon3.png" -> R.drawable.icon3
            "/images/icon4.png" -> R.drawable.icon4
            "/images/icon5.png" -> R.drawable.icon5
            "/images/icon6.png" -> R.drawable.icon6
            "/images/icon7.png" -> R.drawable.icon7
            "/images/icon8.png" -> R.drawable.icon8
            "/images/icon9.png" -> R.drawable.icon9
            "/images/icon10.png" -> R.drawable.icon10
            "/images/icon11.png" -> R.drawable.icon11
            "/images/icon12.png" -> R.drawable.icon12
            "/images/icon13.png" -> R.drawable.icon13
            "/images/icon14.png" -> R.drawable.icon14
            "/images/icon15.png" -> R.drawable.icon15
            else -> R.drawable.default_pfp
        }
    }
}