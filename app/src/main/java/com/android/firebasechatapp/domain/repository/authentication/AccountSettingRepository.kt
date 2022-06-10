package com.android.firebasechatapp.domain.repository.authentication

import com.android.firebasechatapp.resource.SimpleResource

interface AccountSettingRepository {
    suspend fun updateEmail(email: String, password: String): SimpleResource
}