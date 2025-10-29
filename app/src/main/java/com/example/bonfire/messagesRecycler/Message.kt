package com.example.bonfire.messagesRecycler

import com.google.type.DateTime

data class Message(
    val displayName: String,
    val photoURL: String,
    val text: String,
    val timestamp: Any?,
    val uid: String
)