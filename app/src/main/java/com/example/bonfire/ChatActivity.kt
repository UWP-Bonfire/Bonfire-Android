package com.example.bonfire

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bonfire.messagesRecycler.FakeRepository
import com.example.bonfire.messagesRecycler.Message
import com.example.bonfire.messagesRecycler.MessageAdapter
import com.example.bonfire.messagesRecycler.MessageId


class ChatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_layout)

        var recyclerView: RecyclerView = findViewById(R.id.chat_messages_RecyclerView)
        recyclerView.adapter = MessageAdapter(createData())
        recyclerView.layoutManager = LinearLayoutManager(this)


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

    /**
     * Uses the repository to collect the raw data and bundles up those values
     * into our Message data class, something our adapter knows how to work with
     */
    private fun createData(): List<Message> {
        //Get data from the repository
        val names = FakeRepository.messageUserName
        val content = FakeRepository.messageContent
        val profilePicture = FakeRepository.messageProfile

        val messageData = ArrayList<Message>()
        MessageId.entries.forEach { messageID ->
            //If the Id is in all lists, add message to the ArrayList
            if (containsId(messageID, names, profilePicture)) {
                messageData.add(
                    Message(
                        userName = names[messageID]!!,
                        content = content[messageID]!!,
                        userProfile = profilePicture[messageID]!!
                    )
                )
            }
        }

        return messageData
    }

    /**
     * Takes in a molecule id and checks if it is contained within all mappings passed
     */
    private fun containsId(messageID: MessageId, vararg maps: Map<MessageId, Any>): Boolean {
        maps.forEach {
            if (messageID !in it.keys) { return false }
        }
        return true
    }
}