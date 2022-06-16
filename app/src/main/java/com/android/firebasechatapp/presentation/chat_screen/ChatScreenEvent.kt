package com.android.firebasechatapp.presentation.chat_screen

sealed class ChatScreenEvent {
    class CreateNewChatRoom(val name: String, val securityLevel: Int) : ChatScreenEvent()
}