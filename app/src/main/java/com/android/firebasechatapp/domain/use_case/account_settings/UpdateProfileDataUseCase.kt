package com.android.firebasechatapp.domain.use_case.account_settings

import com.android.firebasechatapp.domain.model.account_settings.ProfileData
import com.android.firebasechatapp.domain.model.account_settings.ProfileUpdateResult
import com.android.firebasechatapp.domain.repository.authentication.AccountSettingRepository
import com.android.firebasechatapp.resource.Resource
import javax.inject.Inject

class UpdateProfileDataUseCase @Inject constructor(
    private val accountSettingRepository: AccountSettingRepository
) {
    suspend operator fun invoke(profileData: ProfileData): Resource<ProfileUpdateResult> {
        val validationResult = UpdateProfileDataValidation.validateProfileData(
            name = profileData.name,
            email = profileData.email,
            password = profileData.password
        )
        if (validationResult is Resource.Error) {
            return validationResult
        }
        return accountSettingRepository.updateProfileData(profileData)
    }
}