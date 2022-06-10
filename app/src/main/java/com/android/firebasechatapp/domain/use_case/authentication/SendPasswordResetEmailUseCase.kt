package com.android.firebasechatapp.domain.use_case.authentication

import com.android.firebasechatapp.domain.repository.authentication.AuthenticationRepository
import com.android.firebasechatapp.resource.Resource
import com.android.firebasechatapp.resource.SimpleResource
import com.android.firebasechatapp.resource.UiText
import javax.inject.Inject

class SendPasswordResetEmailUseCase @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) {
    suspend operator fun invoke(email: String): SimpleResource {
        if (email.isEmpty()) {
            return Resource.Error(UiText.DynamicString("Email is empty"))
        }
        return authenticationRepository.sendPasswordResetEmail(email)
    }
}