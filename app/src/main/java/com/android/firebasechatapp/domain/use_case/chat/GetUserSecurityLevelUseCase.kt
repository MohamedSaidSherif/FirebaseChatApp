package com.android.firebasechatapp.domain.use_case.chat

import com.android.firebasechatapp.domain.repository.authentication.AccountSettingRepository
import com.android.firebasechatapp.resource.Resource
import javax.inject.Inject

class GetUserSecurityLevelUseCase @Inject constructor(
    private val accountSettingRepository: AccountSettingRepository
) {
    suspend operator fun invoke(): Resource<Int> = accountSettingRepository.getSecurityLevel()
}