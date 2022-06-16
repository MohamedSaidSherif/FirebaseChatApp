package com.android.firebasechatapp.data.model

data class RemoteChatRoom(
    val chatRoomName: String,
    val creatorId: String,
    val securityLevel: Int,
    val chatRoomId: String,
    val chatRoomMessages: List<RemoteChatMessage>? = null,
    val users: List<String>? = null,
)
