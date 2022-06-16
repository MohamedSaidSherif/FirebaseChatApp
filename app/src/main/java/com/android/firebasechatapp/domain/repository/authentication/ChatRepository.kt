package com.android.firebasechatapp.domain.repository.authentication

import com.android.firebasechatapp.resource.SimpleResource

interface ChatRepository {
    suspend fun createNewChatRoom(name: String, securityLevel: Int): SimpleResource
}