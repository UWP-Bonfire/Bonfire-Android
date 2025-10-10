package com.example.bonfire

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class SignInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signin_layout)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // Button to switch to main screen activity
        val signinButton: Button = findViewById(R.id.signin_button)
        signinButton.setOnClickListener {
            val intent = Intent(this, MessagesListActivity::class.java)
            startActivity(intent)
        }

        // switch to sign in activity
        val switchButton: Button = findViewById(R.id.signin_switch_button)
        switchButton.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }
}

