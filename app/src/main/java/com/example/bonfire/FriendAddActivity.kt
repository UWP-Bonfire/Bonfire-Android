package com.example.bonfire

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore


class FriendAddActivity : AppCompatActivity() {
    val TAG = "friend add"
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.friend_add_layout)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val friendAddButton : Button = findViewById(R.id.friend_add_search_button)
        friendAddButton.setOnClickListener {
            val friendEditText: TextInputEditText = findViewById(R.id.friend_add_edit)
            val friendName = friendEditText.getText().toString()

            sendFriendRequest(friendName)
        }

        // go to chat screen
        val chatButton: ImageButton = findViewById(R.id.menu_button_chat)
        chatButton.setOnClickListener {
            val intent = Intent(this, GroupChatListActivity::class.java)
            startActivity(intent)
            finish()
        }

        // go to back friends screen
        val friendBackButton: ImageButton = findViewById(R.id.friend_add_backArrow)
        friendBackButton.setOnClickListener {
            val intent = Intent(this, FriendListActivity::class.java)
            startActivity(intent)
        }

        // go to friends screen
        val friendButton: ImageButton = findViewById(R.id.menu_button_friends)
        friendButton.setOnClickListener {
            val intent = Intent(this, FriendListActivity::class.java)
            startActivity(intent)
        }

        // go to accounts screen
        val accountButton: ImageButton = findViewById(R.id.menu_button_account)
        accountButton.setOnClickListener {
            val intent = Intent(this, AccountActivity::class.java)
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

    fun sendFriendRequest(friendName:String){
        // validate
        if (friendName == "") {
            Toast.makeText(baseContext,"Please enter a username to search.", Toast.LENGTH_SHORT).show()
            return
        }

        // search for friend
        val docRef = db.collection("users").whereEqualTo("name", friendName)
        var data : Map<String, Any> = mapOf()
        var documentId = ""
        docRef.get()
        .addOnSuccessListener { documents ->
            if (!documents.isEmpty) {
                for (document in documents) {
                    if (friendRequestValid(document.data, document.id)){
                        // All validation passed, make friend request
                        val friendRequestArray = arrayOf(uid, document.id)
                        friendRequestArray.sort()
                        val friendRequestId = friendRequestArray.joinToString("_")

                        val messageData = hashMapOf(
                            "createdAt" to Timestamp.now(),
                            "from" to uid,
                            "status" to "pending",
                            "to" to document.id
                        )

                        // Write friend request to firebase
                        db.collection("friendRequests").document(friendRequestId).set(messageData)
                        Toast.makeText(baseContext, "Friend request sent successfully to ${document.data["name"]}", Toast.LENGTH_SHORT).show()
                    }

                }
            }
            else {
                Toast.makeText(baseContext, "No users found with that exact username.", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "No such document")
            }
        }
        .addOnFailureListener { exception ->
            Log.d(TAG, "get failed with ", exception)
        }
    }

    fun friendRequestValid(data: Map<String, Any>, documentId:String): Boolean{
        if (data.isEmpty() || documentId == "") return false

        // if you tried to friend yourself
        if(documentId == uid){
            Toast.makeText(baseContext, "Why are you trying to friend yourself?", Toast.LENGTH_SHORT).show()
            return false
        }

        // If found a user, check they're not already friends
        for (otherUserFriends in (data["friends"] as List<*>)){
            if (otherUserFriends == uid){
                Toast.makeText(baseContext, "You are already friends with this user.", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Already friends with $uid")
                return false
            }
        }

        return true
    }
}