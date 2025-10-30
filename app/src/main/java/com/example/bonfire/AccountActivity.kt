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
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.account_layout)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

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
                accountAvatarImageView.setImageResource(getAvatarId(avatarPath))
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
                accountAvatarImageView.setImageResource(getAvatarId(avatar))
            }
        }


        // go to chat screen
        val chatButton: ImageButton = findViewById(R.id.menu_button_chat)
        chatButton.setOnClickListener {
            val intent = Intent(this, GroupChatListActivity::class.java)
            startActivity(intent)
        }

        // go to friends screen
        val friendButton: ImageButton = findViewById(R.id.menu_button_friends)
        friendButton.setOnClickListener {
            val intent = Intent(this, FriendListActivity::class.java)
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

    // load avatar, ex."/images/icon1" to R.id.icon1
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

