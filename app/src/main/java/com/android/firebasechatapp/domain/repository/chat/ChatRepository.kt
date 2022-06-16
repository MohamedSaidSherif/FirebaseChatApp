package com.android.firebasechatapp.domain.repository.chat

import com.android.firebasechatapp.domain.model.chat.ChatRoom
import com.android.firebasechatapp.resource.Resource
import com.android.firebasechatapp.resource.SimpleResource

interface ChatRepository {
    suspend fun createNewChatRoom(name: String, securityLevel: Int): SimpleResource
    suspend fun getChatRooms(): Resource<List<ChatRoom>>
}