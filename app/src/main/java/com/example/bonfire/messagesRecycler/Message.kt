package com.example.bonfire.messagesRecycler

import androidx.annotation.DrawableRes
import com.google.type.DateTime

data class Message(
    val displayName: String,
    @DrawableRes val photoURL: Int,
    val text: String,
    val timeStamp: DateTime,
    val uid: String
)