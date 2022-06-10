package com.android.firebasechatapp.domain.use_case.account_settings

import com.android.firebasechatapp.domain.repository.authentication.AccountSettingRepository
import com.android.firebasechatapp.resource.Resource
import com.android.firebasechatapp.resource.SimpleResource
import javax.inject.Inject

class UpdateProfileDataUseCase @Inject constructor(
    private val accountSettingRepository: AccountSettingRepository
) {
    suspend operator fun invoke(email: String, password: String): SimpleResource {
        val validationResult = UpdateProfileDataValidation.validateProfileData(
            email = email,
            password = password
        )
        if (validationResult is Resource.Error) {
            return validationResult
        }
        return accountSettingRepository.updateEmail(email, password)
    }
}