package com.android.firebasechatapp.domain.repository.authentication

import com.android.firebasechatapp.domain.model.account_settings.ProfileUpdateResult
import com.android.firebasechatapp.resource.Resource

interface AccountSettingRepository {
    suspend fun updateProfileData(
        name: String,
        phone: String,
        email: String,
        password: String
    ): Resource<ProfileUpdateResult>
}