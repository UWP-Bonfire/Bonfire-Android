package com.example.bonfire

import android.R.id
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate


class GroupChatListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.groupchat_list_layout)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val loginButton: ImageButton = findViewById(R.id.text_chat_list_message)
        loginButton.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)

            // TODO: Generate list of friends, and open the specific chat message with them
            // if there is no friendID, this is the global chat
            // Passes friendID to chat activity
            val id = ""
            //val id = "oV2wejAGCWfpkfP8blVe4pKRYxH2" //  hardcoded amaice user id
            intent.putExtra("id", id)
            startActivity(intent)
        }
    }
}

