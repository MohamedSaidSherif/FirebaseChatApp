package com.android.firebasechatapp.domain.repository.authentication

import com.android.firebasechatapp.resource.SimpleResource

interface AccountSettingRepository {
    suspend fun updateProfileData(
        name: String,
        phone: String,
        email: String,
        password: String
    ): SimpleResource
}