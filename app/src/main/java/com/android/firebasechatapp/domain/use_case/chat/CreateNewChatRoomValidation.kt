package com.android.firebasechatapp.domain.use_case.chat

import com.android.firebasechatapp.resource.Resource
import com.android.firebasechatapp.resource.SimpleResource
import com.android.firebasechatapp.resource.UiText

object CreateNewChatRoomValidation {

    fun validateNewChatRoomData(
        name: String,
        securityLevel: Int,
        userSecurityLevel: Int
    ): SimpleResource {
        return if (name.isEmpty()) {
            Resource.Error(
                UiText.DynamicString("Name is empty")
            )
        } else if (securityLevel > userSecurityLevel) {
            Resource.Error(
                UiText.DynamicString("insufficient security level")
            )
        } else {
            Resource.Success(Unit)
        }
    }
}