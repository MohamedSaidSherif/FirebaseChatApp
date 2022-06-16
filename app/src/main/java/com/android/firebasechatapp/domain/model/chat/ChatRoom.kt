package com.android.firebasechatapp.domain.model.chat

data class ChatRoom(
    val chatRoomName: String,
    val creatorId: String,
    val securityLevel: Int,
    val chatRoomId: String,
    val chatRoomMessages: List<ChatMessage>,
    val users: List<String>,
)
