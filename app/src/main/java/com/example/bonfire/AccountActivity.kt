package com.example.bonfire

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import androidx.core.view.size
import com.bumptech.glide.Glide
import com.google.firebase.storage.storage


class AccountActivity : AppCompatActivity() {
    lateinit var TAG:String
    val helper = Helper()
    private val channelId = "i.apps.notifications" // Unique channel ID for notifications
    private val description = "Message notification"  // Description for the notification channel

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

                // Create a reference to a file from a Google Cloud Storage URI
                val avatarPath = (data?.get("avatar") ?: "") as String

                helper.setProfilePicture(this, avatarPath, accountAvatarImageView)
            } else {
                Log.d(TAG, "No such document")
            }
        }
        .addOnFailureListener { exception ->
            Log.d(TAG, "get failed with ", exception)
        }

        val storagePath = "gs://bonfire-d8db1.firebasestorage.app/Profile_Pictures/"
        val avatarGrid: GridLayout = findViewById(R.id.account_grid)
        for (i in 0..<avatarGrid.size) {
            val child: ShapeableImageView = (avatarGrid as ViewGroup).getChildAt(i) as ShapeableImageView
            child.setOnClickListener {
                // Update the "avatar" field of the user 
                val userRef = db.collection("users").document(user?.uid ?: "")
                val avatarUri = storagePath + "icon${(i + 1)}.png"

                val storage = Firebase.storage
                val gsReference = storage.getReferenceFromUrl(avatarUri)

                gsReference.downloadUrl.addOnSuccessListener { uri ->
                    // visually change icon
                    Glide.with(this)
                        .load(uri)
                        .into(accountAvatarImageView)

                    // change field in db
                    userRef
                        .update("avatar", uri)
                        .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully updated!") }
                        .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }
                }.addOnFailureListener { e ->
                    Log.e(TAG, "Couldn't get avatar uri: $e")
                }

            }
        }

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        helper.listenForNotifs(uid ?: "", this)

        defineBottomNavButtons()
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

