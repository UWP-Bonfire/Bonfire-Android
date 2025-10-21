package com.example.bonfire

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore


class ChatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_layout)

        // read in friendId to open correct chat
        val b = intent.extras
        val friendId: String? = b!!.getString("id")


        var recyclerView: RecyclerView = findViewById(R.id.chat_messages_RecyclerView)
        recyclerView.adapter = MessageAdapter(createData(friendId))
        recyclerView.layoutManager = LinearLayoutManager(this)


        // Prevent dark mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // Button to switch to main screen activity
        val loginButton: ImageButton = findViewById(R.id.chat_cardView_backArrow)
        loginButton.setOnClickListener {
            val intent = Intent(this, GroupChatListActivity::class.java)
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
    private fun createData(friendId: String?): List<Message> {
        //Get data from the repository
        val names = FakeRepository.messageUserName
        val content = FakeRepository.messageContent
        val profilePicture = FakeRepository.messageProfile

        val db = Firebase.firestore

        // Generate chatId, which is the two users, combined
        // Ex. userId = "abc" and friendId = "bcd" -> chatId = "abc_bcd"

        // Exception is if friendId == null, therefore its the global chat
        var chatId = "chatId"
        var messagesPath = "messages"
        if (friendId != null){
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            val chatIdArray = arrayOf(userId, friendId)
            chatIdArray.sort()

            chatId = chatIdArray.joinToString("_")
            messagesPath = "chats/$chatId/messages"
        }
        Log.i("chat", chatId)

        val messageData = ArrayList<Message>()

        db.collection(messagesPath)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    messageData.add(
                        Message(
                            displayName = document.data['displayName'],
                            text = document.data['photoURL'],
                            photoURL = profilePicture[messageID]!!
                        )
                    )
                    Log.i( "chat", "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w( "Error getting documents: ", exception)
            }

        MessageId.entries.forEach { messageID ->
            //If the Id is in all lists, add message to the ArrayList
            if (containsId(messageID, names, profilePicture)) {
                messageData.add(
                    Message(
                        displayName = names[messageID]!!,
                        text = content[messageID]!!,
                        photoURL = profilePicture[messageID]!!
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