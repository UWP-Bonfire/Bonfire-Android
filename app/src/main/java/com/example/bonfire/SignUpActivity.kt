package com.example.bonfire

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup_layout)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // Button to switch to main screen activity
        val signupButton: Button = findViewById(R.id.signup_button)
        signupButton.setOnClickListener {
            val intent = Intent(this, GroupChatListActivity::class.java)
            startActivity(intent)
        }

        // switch to sign in activity
        val switchButton: Button = findViewById(R.id.signup_switch_button)
        switchButton.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
    }
}

