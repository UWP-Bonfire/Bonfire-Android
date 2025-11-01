package com.example.bonfire

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore


class ChatActivity : AppCompatActivity() {
    val TAG = "chat"
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val db = Firebase.firestore
    private lateinit var chatList: ArrayList<Map<String, Any>?>

    companion object {
        var userAvatars : MutableMap<String, Int> = mutableMapOf()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_layout)

        // read in friendId to open correct chat
        val b = intent.extras
        var friendId: String? = b!!.getString("id")
        if(friendId == ""){
            friendId = null
        }

        // data of user so you don't have to request it every time
        var userData : Map<String, Object> = mapOf()
        val docRef = db.collection("users").document(uid?: "")
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    userData = document.data as Map<String, Object>
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }

        // Recycler view to display messages of chat //

        // Load all users icons into a dictionary (instead of finding their icon FOR EACH message)
        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    // ex. uid : R.drawable.icon1
                    userAvatars.put(document.id, getAvatarId(document.data["avatar"] as String))
                    //Log.d(TAG, "${document.id} => ${document.data["avatar"] as String}")
                }
                // Load chat after all avatars have been "cached"
                chatList = arrayListOf()
                createData(friendId)
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }


        //recyclerView.adapter = MessageAdapter()
        val recyclerView: RecyclerView = findViewById(R.id.chat_messages_RecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)


        // Set chat name (name of person you're talking to)
        // if friendId == null, its the global chat
        if (friendId != null){
            // get name of friend
            val docRef = db.collection("users").document(friendId)
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        //Log.d("chatname", "DocumentSnapshot data: ${document.data}")
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

        // Send message
        // if friendId == null, we are in global chat
        if (friendId == null){
            val sendButton: ImageView = findViewById(R.id.chat_MessageBar_SendButton)
            sendButton.setOnClickListener {
                val emailEditText:  TextInputEditText = findViewById(R.id.chat_MessageBar_TextInputEditText)
                val messageSend = emailEditText.getText().toString()
                if (messageSend != ""){
                    val messageData = hashMapOf(
                        "displayName" to userData["name"],
                        "photoURL" to userData["avatar"],
                        "senderId" to uid,
                        "text" to messageSend,
                        "timestamp" to Timestamp.now(),
                    )
                    db.collection("messages").document().set(messageData)
                }
                emailEditText.setText("")
                recyclerView.scrollToPosition(chatList.size - 1)
            }
        }

        // Prevent dark mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // Button to switch to main screen activity
        val loginButton: ImageButton = findViewById(R.id.chat_cardView_backArrow)
        loginButton.setOnClickListener {
            val intent = Intent(this, GroupChatListActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Reset layout when keyboard pulls up
        val rootView = findViewById<View?>(R.id.chat_content)
        if (rootView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(
                rootView
            ) { v: View?, insets: WindowInsetsCompat? ->
                val imeInsets: Insets = insets!!.getInsets(WindowInsetsCompat.Type.ime())
                val navInsets: Insets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
                val bottomInset: Int = imeInsets.bottom.coerceAtLeast(navInsets.bottom)

                recyclerView.scrollBy(0, bottomInset)
                Log.d("chatname", "scrolled by " + bottomInset)

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
            val chatIdArray = arrayOf(uid, friendId)
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
                    //Log.d(TAG, "data: ${document.data}")
                    chatList.add(document.data)
                }
            } else {
                Log.d(TAG, "data: null")
            }

            val recyclerView: RecyclerView = findViewById(R.id.chat_messages_RecyclerView)
            recyclerView.adapter = MessageAdapter(chatList)
            // scroll to bottom
            recyclerView.scrollToPosition(chatList.size - 1)
        }
    }

    // ex. set message with "avatar" = "/images/icon1" to R.id.icon1
    // --- Avatar mapping --- (terribly hardcoded)
    fun getAvatarId(avatarPath: String) : Int {
        return when (avatarPath) {
            "/images/Logo.png" -> R.drawable.bonfire_icon
            "/images/icon1.png" -> R.drawable.icon1
            "/images/icon2.png" -> R.drawable.icon2
            "/images/icon3.png" -> R.drawable.icon3
            "/images/icon4.png" -> R.drawable.icon4
            "/images/icon5.png" -> R.drawable.icon5
            "/images/icon6.png" -> R.drawable.icon6
            "/images/icon7.png" -> R.drawable.icon7
            "/images/icon8.png" -> R.drawable.icon8
            "/images/icon9.png" -> R.drawable.icon9
            "/images/icon10.png" -> R.drawable.icon10
            "/images/icon11.png" -> R.drawable.icon11
            "/images/icon12.png" -> R.drawable.icon12
            "/images/icon13.png" -> R.drawable.icon13
            "/images/icon14.png" -> R.drawable.icon14
            "/images/icon15.png" -> R.drawable.icon15
            else -> R.drawable.default_pfp
        }
    }
}