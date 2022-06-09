package com.android.firebasechatapp.domain.use_case

import com.android.firebasechatapp.domain.repository.authentication.AuthenticationRepository
import com.android.firebasechatapp.resource.Resource
import com.google.firebase.auth.AuthResult
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) {

    suspend operator fun invoke(
        email: String,
        password: String,
        confirmPassword: String
    ): Resource<AuthResult> {
        val validationResult = AuthenticationValidation.validateRegistrationForm(email, password, confirmPassword)
        if (validationResult is Resource.Error) {
            return validationResult
        }
        return authenticationRepository.register(email, password)
    }
}