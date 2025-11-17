package com.example.bonfire

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import androidx.core.view.size


class AccountActivity : AppCompatActivity() {
    lateinit var TAG:String
    val helper = Helper()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.account_layout)

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

        // TODO: add button listeners or whatever to account_grid
        val avatarGrid: GridLayout = findViewById(R.id.account_grid)
        for (i in 0..<avatarGrid.size) {
            val child: ShapeableImageView = (avatarGrid as ViewGroup).getChildAt(i) as ShapeableImageView
            child.setOnClickListener {
                // Update the "avatar" field of the user 
                val userRef = db.collection("users").document(user?.uid ?: "")
                val avatar = "/images/icon" + (i + 1).toString()  + ".png"
                userRef
                    .update("avatar", avatar)
                    .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully updated!") }
                    .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }
                // visually change icon
                val accountAvatarImageView: ShapeableImageView = findViewById(R.id.account_avatar)
                val helper = Helper()
                accountAvatarImageView.setImageResource(helper.getAvatarId(avatar))
            }
        }

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

