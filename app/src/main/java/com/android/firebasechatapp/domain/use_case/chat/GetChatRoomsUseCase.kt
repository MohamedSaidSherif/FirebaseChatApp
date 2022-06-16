package com.android.firebasechatapp.domain.use_case.chat

import com.android.firebasechatapp.domain.model.chat.ChatRoom
import com.android.firebasechatapp.domain.repository.chat.ChatRepository
import com.android.firebasechatapp.resource.Resource
import javax.inject.Inject

class GetChatRoomsUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(): Resource<List<ChatRoom>> = chatRepository.getChatRooms()
}