package com.android.firebasechatapp.domain.model.chat

data class ChatMessage(
    val message: String,
    val userId: String,
    val timestamp: String,
    val profileImage: String,
    val name: String,
)
