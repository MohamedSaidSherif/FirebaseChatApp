package com.android.firebasechatapp.presentation.chat_screen

import com.android.firebasechatapp.domain.model.chat.ChatRoom

sealed class ChatScreenState {
    object NewChatRoomIsCreated: ChatScreenState()
    class ChatRooms(val chatRooms: List<ChatRoom>): ChatScreenState()
}