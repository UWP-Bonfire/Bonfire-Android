package com.example.bonfire

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth

class GroupChatListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.groupchat_list_layout)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // for testing
        val user = FirebaseAuth.getInstance().currentUser
        Toast.makeText(baseContext, "Signed in! Welcome " + user?.email, Toast.LENGTH_SHORT).show()

        val openChatButton: ImageButton = findViewById(R.id.text_chat_list_message)
        openChatButton.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            startActivity(intent)
        }


        // account
        val accountButton: ImageButton = findViewById(R.id.menu_button_account)
        accountButton.setOnClickListener {
            // go back to welcome screen
            val intent = Intent(this, AccountActivity::class.java)
            startActivity(intent)
        }

        // log out
        val logoutButton: ImageButton = findViewById(R.id.menu_button_logout)
        logoutButton.setOnClickListener {
            // go back to welcome screen
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
        }


    }
}

