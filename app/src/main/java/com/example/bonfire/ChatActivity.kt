package com.example.bonfire

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bonfire.MessageAdapter
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore


class ChatActivity : AppCompatActivity() {
    val TAG = "chat"
    private lateinit var chatList: ArrayList<Map<String, Any>?>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_layout)


        // read in friendId to open correct chat
        val b = intent.extras
        var friendId: String? = b!!.getString("id")
        if(friendId == ""){
            friendId = null
        }

        // Recycler view to display messages of chat

        chatList = arrayListOf()
        createData(friendId)
        //recyclerView.adapter = MessageAdapter()
        val recyclerView: RecyclerView = findViewById(R.id.chat_messages_RecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)


        // Set chat name (name of person you're talking to)
        // if friendId == null, its the global chat
        if (friendId != null){
            // get name of friend
            val db = Firebase.firestore
            val docRef = db.collection("users").document(friendId)
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        Log.d("chatname", "DocumentSnapshot data: ${document.data}")
                        val data = document.data
                        val chatName : TextView = findViewById(R.id.chat_cardView_UserName)
                        chatName.text = (data?.get("displayName") ?: "") as String
                    } else {
                        Log.d("chatname", "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("chatname", "get failed with ", exception)
                }
        }

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
    private fun createData(friendId: String?){
        val db = Firebase.firestore

        // Generate chatId, which is the two users, combined
        // And generate messagesPath depending if global or private
        // Ex. userId = "abc" and friendId = "bcd" -> chatId = "abc_bcd"
        var chatId = "chatId"
        var messagesPath = "messages"

        // Exception: if friendId == null, its the global chat
            if (friendId != null){
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            val chatIdArray = arrayOf(userId, friendId)
            chatIdArray.sort()

            chatId = chatIdArray.joinToString("_")
            messagesPath = "chats/$chatId/messages"
        }
        Log.i(TAG, chatId)


        //val friendDocument = getDocumentReference(db, "users", friendId.toString())

        // Reads messages from chats/[chatId]/messages (global chat) into messageData arrayList
        //val messageData = ArrayList<Map<String, Any>?>()
        val messagesRef = db.collection(messagesPath).orderBy("timestamp")
        messagesRef.addSnapshotListener { snapshot, e ->
            chatList.clear()
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
            }

            if (snapshot != null && !snapshot.isEmpty) {
                for (document in snapshot.documents) {
                    Log.d(TAG, "data: ${document.data}")
                    chatList.add(document.data)
                }
            } else {
                Log.d(TAG, "data: null")
            }

            val recyclerView: RecyclerView = findViewById(R.id.chat_messages_RecyclerView)
            recyclerView.adapter = MessageAdapter(chatList)
        }
    }
}