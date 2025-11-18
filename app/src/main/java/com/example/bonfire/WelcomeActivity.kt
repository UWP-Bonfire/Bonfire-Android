package com.example.bonfire

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.welcome_layout)

        // Button to switch to sign in activity
        val signInButton: Button = findViewById(R.id.welcome_signin_button)
        signInButton.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        // Button to switch to  sign up screen activity
        val signUpButton: Button = findViewById(R.id.welcome_signup_button)
        signUpButton.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        val user = Firebase.auth.currentUser
        if (user != null) {
            // User is signed in
            val intent = Intent(this, GroupChatListActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}

