package com.android.firebasechatapp.domain.use_case.authentication

import com.android.firebasechatapp.domain.repository.authentication.AuthenticationRepository
import javax.inject.Inject

class IsUserSignedInUseCase @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) {

    suspend operator fun invoke(): Boolean = authenticationRepository.isUserSignedIn()
}