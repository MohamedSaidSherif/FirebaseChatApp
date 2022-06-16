package com.android.firebasechatapp.data.model

import com.android.firebasechatapp.domain.model.chat.ChatMessage

data class RemoteChatMessage(
    val message: String? = null,
    val userId: String? = null,
    val timestamp: String? = null,
    val profileImage: String? = null,
    val name: String? = null,
)

fun RemoteChatMessage.toDomain() = ChatMessage(
    message = message ?: "",
    userId = userId ?: "",
    timestamp = timestamp ?: "",
    profileImage = profileImage ?: "",
    name = name ?: "",
)

fun List<RemoteChatMessage>.toDomain(): List<ChatMessage> {
    val chatMessages = mutableListOf<ChatMessage>()
    for (remoteChatMessage in this) {
        chatMessages.add(remoteChatMessage.toDomain())
    }
    return chatMessages
}