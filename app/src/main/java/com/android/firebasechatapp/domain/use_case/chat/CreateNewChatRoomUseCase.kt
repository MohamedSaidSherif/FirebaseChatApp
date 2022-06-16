package com.android.firebasechatapp.domain.use_case.chat

import com.android.firebasechatapp.domain.repository.authentication.ChatRepository
import com.android.firebasechatapp.resource.Resource
import com.android.firebasechatapp.resource.SimpleResource
import javax.inject.Inject

class CreateNewChatRoomUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(
        name: String,
        securityLevel: Int,
        userSecurityLevel: Int
    ): SimpleResource {
        val validationResult = CreateNewChatRoomValidation.validateNewChatRoomData(
            name = name,
            securityLevel = securityLevel,
            userSecurityLevel = userSecurityLevel
        )
        if (validationResult is Resource.Error) {
            return validationResult
        } else {
            return chatRepository.createNewChatRoom(
                name = name,
                securityLevel = securityLevel
            )
        }
    }
}