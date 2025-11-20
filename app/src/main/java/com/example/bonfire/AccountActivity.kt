package com.example.bonfire

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import androidx.core.view.size
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentChange


class AccountActivity : AppCompatActivity() {
    lateinit var TAG:String
    val helper = Helper()
    private val channelId = "i.apps.notifications" // Unique channel ID for notifications
    private val description = "Test notification"  // Description for the notification channel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.account_layout)

        val notificationChannel = NotificationChannel(
            channelId,
            description,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            enableLights(true) // Turn on notification light
            lightColor = Color.GREEN
            enableVibration(true) // Allow vibration for notifications
        }

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)

        val user = FirebaseAuth.getInstance().currentUser
        val accountUserText: TextView = findViewById(R.id.account_user)
        val accountEmailText: TextView = findViewById(R.id.account_email)
        val accountAvatarImageView: ShapeableImageView = findViewById(R.id.account_avatar)
        
        TAG = "account_activity"
        
        val db = Firebase.firestore
        // get details of account
        val docRef = db.collection("users").document(user?.uid ?: "")
        docRef.get()
        .addOnSuccessListener { document ->
            if (document != null) {
                Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                val data = document.data
                accountEmailText.text = (data?.get("email") ?: "") as String
                accountUserText.text = (data?.get("name") ?: "") as String

                val avatarPath = data?.get("avatar")?.toString()
                accountAvatarImageView.setImageResource(helper.getAvatarId(avatarPath))
            } else {
                Log.d(TAG, "No such document")
            }
        }
        .addOnFailureListener { exception ->
            Log.d(TAG, "get failed with ", exception)
        }

        val avatarGrid: GridLayout = findViewById(R.id.account_grid)
        for (i in 0..<avatarGrid.size) {
            val child: ShapeableImageView = (avatarGrid as ViewGroup).getChildAt(i) as ShapeableImageView
            child.setOnClickListener {
                // Update the "avatar" field of the user 
                val userRef = db.collection("users").document(user?.uid ?: "")
                val avatar = "/bonfire-backend/src/assets/icons/icon${(i + 1)}.png"
                userRef
                    .update("avatar", avatar)
                    .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully updated!") }
                    .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }
                // visually change icon
                val accountAvatarImageView: ShapeableImageView = findViewById(R.id.account_avatar)
                accountAvatarImageView.setImageResource(helper.getAvatarId(avatar))
            }
        }

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        getListOfUserChatIDs(uid ?: "")
        defineBottomNavButtons()

        val notifButton: Button = findViewById(R.id.button_test)
        notifButton.setOnClickListener {
            Log.d(TAG, "Requesting perms")

            // Request runtime permission for notifications on Android 13 and higher
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                        101
                    )
                    return@setOnClickListener
                }
            }

            val helper = Helper()
            Log.d(TAG, "sending notif through test button")
            helper.sendNotification("title", "text", this) // Trigger the notification
        }
    }

    // Return list of dictionaries, each containing:
    //      friends ID, name, avatar, and documentPath of private chat with user
    fun getListOfUserChatIDs(uid:String){
        val listOfFriends : MutableList<Map<String, String>> = mutableListOf()
        val db = Firebase.firestore

        // get list of user's friends
        val userRef = db.collection("users").document(uid)
        userRef.get()
        .addOnSuccessListener { document ->
            if (document != null) {
                val userData = document.data as MutableMap<String, Object>
                val userFriends = userData["friends"] as List<String>
                Log.d(TAG, "friends found ${userData["friends"]}")
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
                            Log.d(TAG, "created dictionary of friend data: $friendDictionary")
                        } else {
                            Log.d(TAG, "No such document")
                        }
                        Log.d(TAG, "ummm $listOfFriends")
                        listenForNotifs(listOfFriends)
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

    fun listenForNotifs(list:MutableList<Map<String, String>>){
        // add listener for each DM with a friend, listening for any messages sent
        Log.d(TAG, "uhhh $list")

        var i = 0
        for (friend in list){
            i += 1
            Log.d(TAG, "adding listener to chat with ${friend["name"]}, groupchatId:${friend["documentPath"]}")
            Firebase.firestore.collection(friend["documentPath"] ?: "")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    return@addSnapshotListener
                }
                for (dc in snapshots!!.documentChanges) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        sendNotif(friend["name"].toString(),
                            dc.document.data["text"].toString(),
                            this
                        )
                        Log.d(TAG, "New added (listener $i): ${dc.document.data}")
                        break
                    }
                }
            }
        }
    }

    private fun sendNotif(title:String, text:String, context: Context) {
        val helper = Helper()
        helper.sendNotification(title, text, context) // Trigger the notification
    }

    private fun defineBottomNavButtons() {
        // go to chat screen
        val chatButton: ImageButton = findViewById(R.id.menu_button_chat)
        chatButton.setOnClickListener {
            val intent = Intent(this, GroupChatListActivity::class.java)
            startActivity(intent)
        }

        // go to friends screen
        val friendButton: ImageButton = findViewById(R.id.menu_button_friends)
        friendButton.setOnClickListener {
            val intent = Intent(this, FriendAddActivity::class.java)
            startActivity(intent)
        }

        // log out
        val logoutButton: ImageButton = findViewById(R.id.menu_button_logout)
        logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}

