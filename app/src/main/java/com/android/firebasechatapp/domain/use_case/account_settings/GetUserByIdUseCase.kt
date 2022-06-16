package com.android.firebasechatapp.domain.use_case.account_settings

import com.android.firebasechatapp.domain.model.User
import com.android.firebasechatapp.domain.repository.authentication.AccountSettingRepository
import com.android.firebasechatapp.resource.Resource
import javax.inject.Inject

class GetUserByIdUseCase @Inject constructor(
    private val accountSettingRepository: AccountSettingRepository
) {
    suspend operator fun invoke(userId: String): Resource<User> = accountSettingRepository.getUserById(userId)
}