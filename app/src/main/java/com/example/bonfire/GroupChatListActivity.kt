package com.example.bonfire

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore


class GroupChatListActivity : AppCompatActivity() {
    val TAG = "chat list"
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val db = Firebase.firestore
    val helper = Helper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.groupchat_list_layout)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        var userData : Map<String, Object>
        val userRef = db.collection("users").document(uid?: "")
        userRef.get()
        .addOnSuccessListener { document ->
            if (document != null) {
                userData = document.data as Map<String, Object>
                populateFriendList(db, userData["friends"] as List<String>)
                Log.d(TAG, "${userData["friends"].toString()} user friend list found")
            } else {
                Log.d(TAG, "No such document")
            }
        }
        .addOnFailureListener { exception ->
            Log.d(TAG, "get failed with ", exception)
        }

        // Open global chat button
        val openChatButton: ImageButton = findViewById(R.id.text_chat_list_message)
        openChatButton.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            startActivity(intent)
            finish()
        }

        defineBottomNavButtons()
    }

    // TODO
    // Generate list of friends, with a button that will open the specific private message message with them
    private fun populateFriendList(db: FirebaseFirestore, userFriends:List<String>) {
        for (friendId in userFriends) {
            Log.d("chat list", "friendId $friendId")

            // Find data of friend
            val docRef = db.collection("users").document(friendId)
            docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val friendData = document.data
                    // Friend data found
                    // dynamically generate friend view in list
                    val groupChatList : LinearLayout = findViewById(R.id.list_messages_LinearLayout)
                    val friendView = LayoutInflater.from(this).inflate(R.layout.groupchat_layout, null, false)

                    val friendName : TextView = friendView.findViewById(R.id.text_chat_list_user)
                    friendName.text = friendData?.get("name") as String

                    val friendAvatar : ShapeableImageView = friendView.findViewById(R.id.friend_profile)
                    friendAvatar.setImageResource(helper.getAvatarId(friendData["avatar"] as String?))

                    // Generate button listener that will open chat with friend
                    val openChatButton: ImageButton = friendView.findViewById(R.id.text_chat_list_message)
                    openChatButton.setOnClickListener {
                        val intent = Intent(this, ChatActivity::class.java)

                        // Passes friendID to chat activity
                        intent.putExtra("id", friendId)
                        ContextCompat.startActivity(this, intent, null)
                    }

                    groupChatList.addView(friendView)
                } else {
                    Log.d("chat list", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("chat list", "get failed with ", exception)
            }
        }
    }

    private fun defineBottomNavButtons() {
        // go to friends screen
        val friendButton: ImageButton = findViewById(R.id.menu_button_friends)
        friendButton.setOnClickListener {
            val intent = Intent(this, FriendAddActivity::class.java)
            startActivity(intent)
            finish()
        }

        // go to accounts screen
        val accountButton: ImageButton = findViewById(R.id.menu_button_account)
        accountButton.setOnClickListener {
            val intent = Intent(this, AccountActivity::class.java)
            startActivity(intent)
            finish()
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