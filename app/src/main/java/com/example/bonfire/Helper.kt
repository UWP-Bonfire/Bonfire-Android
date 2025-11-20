package com.example.bonfire

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewManager
import android.widget.LinearLayout
import android.widget.RemoteViews
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class Helper: AppCompatActivity() {
    private val TAG = "Helper"
    private val channelId = "i.apps.notifications" // Unique channel ID for notifications
    private val description = "Test notification"  // Description for the notification channel
    private val notificationId = 1234 // Unique identifier for the notification

    // ex. set message with "avatar" = "/images/icon1" to R.id.icon1
    // --- Avatar mapping --- (terribly hardcoded)
    fun getAvatarId(avatarPath: String?) : Int {
        return when (avatarPath) {
            "/images/Logo.png" -> R.drawable.bonfire_icon
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

    // Return list of dictionaries, each containing:
    //      friends ID, name, avatar, and documentPath of private chat with user
//    fun getListOfUserChatIDs(uid:String) : MutableList<Map<String, String>>{
//        val listOfFriends : MutableList<Map<String, String>> = mutableListOf()
//        val db = Firebase.firestore
//
//        // get list of user's friends
//        val userRef = db.collection("users").document(uid)
//        userRef.get()
//        .addOnSuccessListener { document ->
//            if (document != null) {
//                val userData = document.data as MutableMap<String, Object>
//                val userFriends = userData["friends"] as List<String>
//                Log.d(TAG, "friends found ${userData["friends"]}")
//                for (friend in userFriends){
//                    // get data of friend
//                    var friendData : Map<String, Any>
//                    val docRef = db.collection("users").document(friend)
//                    docRef.get()
//                    .addOnSuccessListener { friendDoc ->
//                        if (friendDoc != null) {
//                            val friendDictionary : MutableMap<String, String> = mutableMapOf()
//                            friendData = friendDoc.data!!
//                            //Log.d(TAG, "data of friend: ${friendData["name"]} found")
//
//                            // make dictionary of friends ID, avatar, and documentPath of private chat with user
//                            val chatIdArray = arrayOf(uid, friend)
//                            chatIdArray.sort()
//                            val chatId = chatIdArray.joinToString("_")
//
//                            friendDictionary["documentPath"] = "chats/$chatId/messages"
//                            friendDictionary["name"] = friendData["name"].toString()
//                            friendDictionary["friendId"] = friend
//                            friendDictionary["friendAvatar"] = friendData["avatar"].toString()
//                            listOfFriends.add(friendDictionary)
//                            //Log.d(TAG, "created dictionary of friend data: $friendDictionary")
//                        } else {
//                            Log.d(TAG, "No such document")
//                        }
//                    }
//                    .addOnFailureListener { exception ->
//                        Log.d(TAG, "get failed with ", exception)
//                    }
//
//                }
//                return listOfFriends
//
//            } else {
//                Log.d(TAG, "No such document")
//            }
//        }
//        .addOnFailureListener { exception ->
//            Log.d(TAG, "get failed with ", exception)
//        }
//        return mutableListOf()
//    }

    /**
     * Build and send a notification with a custom layout and action.
     */
    @SuppressLint("MissingPermission")
    fun sendNotification(title:String, contextText:String, context:Context) {
        // Intent that triggers when the notification is tapped
        val intent = Intent(context, ChatActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build the notification
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.bonfire_icon) // Notification icon
            .setContentTitle(title) // Title displayed in the notification
            .setContentText(contextText) // Text displayed in the notification
            .setContentIntent(pendingIntent) // Pending intent triggered when tapped
            .setAutoCancel(true) // Dismiss notification when tapped
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Notification priority for better visibility

        // Display the notification
        with(NotificationManagerCompat.from(context)) {
            notify(
                notificationId,
                builder.build()
            )
        }
    }
}