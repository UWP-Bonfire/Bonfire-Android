package com.example.bonfire

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.firestore
import kotlin.collections.set

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

            "/bonfire-backend/src/assets/icons/icon1.png" -> R.drawable.icon1
            "/bonfire-backend/src/assets/icons/icon2.png" -> R.drawable.icon2
            "/bonfire-backend/src/assets/icons/icon3.png" -> R.drawable.icon3
            "/bonfire-backend/src/assets/icons/icon4.png" -> R.drawable.icon4
            "/bonfire-backend/src/assets/icons/icon5.png" -> R.drawable.icon5
            "/bonfire-backend/src/assets/icons/icon6.png" -> R.drawable.icon6
            "/bonfire-backend/src/assets/icons/icon7.png" -> R.drawable.icon7
            "/bonfire-backend/src/assets/icons/icon8.png" -> R.drawable.icon8
            "/bonfire-backend/src/assets/icons/icon9.png" -> R.drawable.icon9
            "/bonfire-backend/src/assets/icons/icon10.png" -> R.drawable.icon10
            "/bonfire-backend/src/assets/icons/icon11.png" -> R.drawable.icon11
            "/bonfire-backend/src/assets/icons/icon12.png" -> R.drawable.icon12
            "/bonfire-backend/src/assets/icons/icon13.png" -> R.drawable.icon13
            "/bonfire-backend/src/assets/icons/icon14.png" -> R.drawable.icon14
            "/bonfire-backend/src/assets/icons/icon15.png" -> R.drawable.icon15

            else -> R.drawable.default_pfp
        }
    }

    fun attemptNotification(title:String, contextText:String, context:Context, friendId:String){
        // Request runtime permission for notifications on Android 13 and higher
        val activity = context as? Activity ?: return

        // Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    101
                )
                return
            }
        }

        // If permission is already granted or OS < 13
        sendNotification(title, contextText, activity, friendId)
    }

    /**
     * Build and send a notification with a custom layout and action.
     */
    @SuppressLint("MissingPermission")
    fun sendNotification(title:String, contextText:String, context:Context, friendId:String) {
        // Intent that triggers when the notification is tapped
        val intent = Intent(context, ChatActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        // Passes friendID to chat activity
        intent.putExtra("id", friendId)

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

    // Creates list of dictionaries, each containing:
    //      friends ID, name, avatar, and documentPath of private chat with user
    // Then calls createNotificationListeners() with list of dictionaries
    fun listenForNotifs(uid:String, context:Context){
        val listOfFriends : MutableList<Map<String, String>> = mutableListOf()
        val db = Firebase.firestore

        // get list of user's friends
        val userRef = db.collection("users").document(uid)
        userRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val userData = document.data as MutableMap<String, Object>
                    val userFriends = userData["friends"] as List<String>
                    Log.d(TAG, "friends found $userFriends")
                    for (friend in userFriends){
                        // get data of friend
                        var friendData : Map<String, Any>
                        val docRef = db.collection("users").document(friend)
                        docRef.get()
                        .addOnSuccessListener { friendDoc ->
                            if (friendDoc != null) {
                                val friendDictionary : MutableMap<String, String> = mutableMapOf()
                                friendData = friendDoc.data!!
                                //Log.d(TAG, "data of friend: ${friendData["name"]} found")

                                // make dictionary of friends ID, avatar, and documentPath of private chat with user
                                val chatIdArray = arrayOf(uid, friend)
                                chatIdArray.sort()
                                val chatId = chatIdArray.joinToString("_")

                                friendDictionary["documentPath"] = "chats/$chatId/messages"
                                friendDictionary["name"] = friendData["name"].toString()
                                friendDictionary["friendId"] = friend
                                friendDictionary["friendAvatar"] = friendData["avatar"].toString()
                                listOfFriends.add(friendDictionary)

                                // once finished (aka after the last friend)
                                // this strange logic is to avoid async issues simply
                                Log.d(TAG, "listOfFriends.size: ${listOfFriends.size}")
                                if (listOfFriends.size == userFriends.size){
                                    Log.d(TAG, "created dictionaries of friend data: $listOfFriends")
                                    // create the notification listeners for each friend
                                    createNotificationListeners(listOfFriends, context, uid)
                                }
                            } else {
                                Log.d(TAG, "No such document")
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.d(TAG, "get failed with ", exception)
                        }
                    }
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }

    fun createNotificationListeners(list:MutableList<Map<String, String>>, context: Context, uid:String){
        // add listener for each DM with a friend, listening for any messages sent
        Log.d(TAG, "createNotificationListeners() called $list")

        var i = 0
        val now = Timestamp.now()
        for (friend in list){
            i += 1

            Firebase.firestore.collection(friend["documentPath"] ?: "")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    return@addSnapshotListener
                } else{
                    Log.w(TAG,  "addSnapshotListener $i: $e")
                }
                Log.d(TAG, "adding listener $i to chat with '${friend["name"]}', groupchatId:'${friend["documentPath"]}'")
                for (dc in snapshots!!.documentChanges) {
                    val messageTimestamp = dc.document.data["timestamp"] as Timestamp
                    if (dc.type == DocumentChange.Type.ADDED
                        && messageTimestamp > now
                        && dc.document.data["senderId"] != uid) {
                        Log.d(TAG, "Received notification from '${friend["name"]}'")
                        attemptNotification(friend["name"].toString(),
                            dc.document.data["text"].toString(),
                            context,
                            friend["friendId"].toString()
                        )
                    }
                    break
                }
            }
        }
    }
}