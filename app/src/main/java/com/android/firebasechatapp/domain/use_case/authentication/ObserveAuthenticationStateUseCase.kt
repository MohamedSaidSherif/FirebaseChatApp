package com.android.firebasechatapp.domain.use_case.authentication

import com.android.firebasechatapp.domain.model.authentication.AuthenticationState
import com.android.firebasechatapp.domain.repository.authentication.AuthenticationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveAuthenticationStateUseCase @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) {
    suspend operator fun invoke(): Flow<AuthenticationState> = authenticationRepository.observeAuthState()
}