package com.android.firebasechatapp.presentation.chat_screen

sealed class ChatScreenState {
    object NewChatRoomIsCreated: ChatScreenState()
}