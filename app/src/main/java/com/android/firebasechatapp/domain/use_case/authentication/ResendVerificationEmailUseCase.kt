package com.android.firebasechatapp.domain.use_case.authentication

import com.android.firebasechatapp.domain.repository.authentication.AuthenticationRepository
import com.android.firebasechatapp.resource.Resource
import com.android.firebasechatapp.resource.SimpleResource
import javax.inject.Inject

class ResendVerificationEmailUseCase @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) {

    suspend operator fun invoke(email: String, password: String): SimpleResource {
        val validationResult = AuthenticationValidation.validateLoginForm(email, password)
        if (validationResult is Resource.Error) {
            return validationResult
        }
        return authenticationRepository.resendVerificationEmail(email, password)
    }
}