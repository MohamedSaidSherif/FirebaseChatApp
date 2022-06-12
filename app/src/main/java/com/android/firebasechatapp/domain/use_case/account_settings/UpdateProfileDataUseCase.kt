package com.android.firebasechatapp.domain.use_case.account_settings

import com.android.firebasechatapp.domain.model.account_settings.ProfileUpdateResult
import com.android.firebasechatapp.domain.repository.authentication.AccountSettingRepository
import com.android.firebasechatapp.resource.Resource
import javax.inject.Inject

class UpdateProfileDataUseCase @Inject constructor(
    private val accountSettingRepository: AccountSettingRepository
) {
    suspend operator fun invoke(
        name: String,
        phone: String,
        email: String,
        password: String
    ): Resource<ProfileUpdateResult> {
        val validationResult = UpdateProfileDataValidation.validateProfileData(
            name = name,
            email = email,
            password = password
        )
        if (validationResult is Resource.Error) {
            return validationResult
        }
        return accountSettingRepository.updateProfileData(
            name = name,
            phone = phone,
            email = email,
            password = password
        )
    }
}