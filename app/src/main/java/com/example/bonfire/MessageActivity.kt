package com.example.bonfire

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class MessageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.message_layout)

        // Prevent dark mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // Button to switch to main screen activity
        val loginButton: ImageButton = findViewById(R.id.chat_cardView_backArrow)
        loginButton.setOnClickListener {
            val intent = Intent(this, MessagesListActivity::class.java)
            startActivity(intent);
        }

        // Reset layout when keyboard pulls up
        val rootView = findViewById<View?>(android.R.id.content)
        if (rootView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(
                rootView
            ) { v: View?, insets: WindowInsetsCompat? ->
                val imeInsets: Insets = insets!!.getInsets(WindowInsetsCompat.Type.ime())
                val navInsets: Insets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
                val bottomInset: Int = imeInsets.bottom.coerceAtLeast(navInsets.bottom)

                rootView.setPadding(
                    navInsets.left,
                    navInsets.top,
                    navInsets.right,
                    bottomInset
                )
                insets
            }
        }
    }
}