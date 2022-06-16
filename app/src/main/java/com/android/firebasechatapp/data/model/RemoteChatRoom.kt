package com.android.firebasechatapp.data.model

import com.android.firebasechatapp.domain.model.chat.ChatRoom

data class RemoteChatRoom(
    val chatRoomId: String,
    val chatRoomName: String,
    val creatorId: String,
    val securityLevel: Int,
    val chatRoomMessages: List<RemoteChatMessage>? = null,
    val users: List<String>? = null,
)

fun RemoteChatRoom.toDomain() = ChatRoom(
    chatRoomName = chatRoomName,
    creatorId = creatorId,
    securityLevel = securityLevel,
    chatRoomId = chatRoomId,
    chatRoomMessages = chatRoomMessages?.toDomain() ?: listOf(),
    users = users ?: listOf()
)
