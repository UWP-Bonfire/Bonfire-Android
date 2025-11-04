package com.example.bonfire

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore


class GroupChatListActivity : AppCompatActivity() {
    val TAG = "chat list"
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val db = Firebase.firestore

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
            } else {
                Log.d(TAG, "No such document")
            }
        }
        .addOnFailureListener { exception ->
            Log.d(TAG, "get failed with ", exception)
        }

        defineBottomNavButtons()
    }

    // TODO
    // Generate list of friends, with a button that will open the specific private message message with them
    private fun populateFriendList(db: FirebaseFirestore, userFriends:List<String>) {
        for (friendId in userFriends) {
            // Find data of friend
            val docRef = db.collection("users").document(friendId)
            docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    // Friend data found
                    //val friendData = document.data as Map<String, Object>

                    val openChatButton: ImageButton = findViewById(R.id.text_chat_list_message)
                    openChatButton.setOnClickListener {
                        val intent = Intent(this, ChatActivity::class.java)

                        // if there is no friendID, this is the global chat
                        // Passes friendID to chat activity
                        //val id = "oV2wejAGCWfpkfP8blVe4pKRYxH2" //  hardcoded amaice user id
                        intent.putExtra("id", friendId)
                        Log.d("chat list", "friendId $friendId")
                        ContextCompat.startActivity(this, intent, null)
                    }
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

