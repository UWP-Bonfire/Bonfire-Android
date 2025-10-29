package com.example.bonfire

import android.R.id
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth


class GroupChatListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.groupchat_list_layout)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val openChatButton: ImageButton = findViewById(R.id.text_chat_list_message)
        openChatButton.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)

            // TODO: Generate list of friends, and open the specific chat message with them
            // if there is no friendID, this is the global chat
            // Passes friendID to chat activity
            val id = ""
            //val id = "oV2wejAGCWfpkfP8blVe4pKRYxH2" //  hardcoded amaice user id
            intent.putExtra("id", id)
            startActivity(intent)
        }


        // go to friends screen
        val friendButton: ImageButton = findViewById(R.id.menu_button_friends)
        friendButton.setOnClickListener {
            val intent = Intent(this, FriendListActivity::class.java)
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

