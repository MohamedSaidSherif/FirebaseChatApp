package com.android.firebasechatapp.domain.use_case.authentication

import com.android.firebasechatapp.domain.repository.authentication.AuthenticationRepository
import com.android.firebasechatapp.resource.SimpleResource
import javax.inject.Inject

class SignOutUseCase @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) {
    suspend operator fun invoke(): SimpleResource = authenticationRepository.signOut()
}