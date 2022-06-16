package com.android.firebasechatapp.presentation.chat_screen

import com.android.firebasechatapp.domain.model.User

sealed class ChatScreenEvent {
    class CreateNewChatRoom(val name: String, val securityLevel: Int) : ChatScreenEvent()
    class GetUserById(val userId: String, val onUserRetrieved: (user: User) -> Unit) : ChatScreenEvent()
}