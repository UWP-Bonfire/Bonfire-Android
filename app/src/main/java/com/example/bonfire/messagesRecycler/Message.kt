package com.example.bonfire.messagesRecycler

import androidx.annotation.DrawableRes

data class Message(
    val userName: String,
    @DrawableRes val userProfile: Int,
    val content: String
)