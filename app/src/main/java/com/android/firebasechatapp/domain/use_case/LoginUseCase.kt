package com.android.firebasechatapp.domain.use_case

import com.android.firebasechatapp.domain.repository.authentication.AuthenticationRepository
import com.android.firebasechatapp.resource.Resource
import com.google.firebase.auth.AuthResult
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) {

    suspend operator fun invoke(email: String, password: String): Resource<AuthResult> {
        val validationResult = AuthenticationValidation.validateLoginForm(email, password)
        if (validationResult is Resource.Error) {
            return validationResult
        }
        return authenticationRepository.login(email, password)
    }
}